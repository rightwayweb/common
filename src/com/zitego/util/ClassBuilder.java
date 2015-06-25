package com.zitego.util;

import java.io.*;
import java.util.*;

/**
 * This will build a base class for you given a class name and a propertiesfile containing the
 * property names and the type they are (or setting them manually). If youare using a properties
 * file to construct the class, then the following properties areapplicable.<br><br>
 * package - Optional property to set the package.<br>
 * imports - Optional property to set imports (use commas to delimit).<br>
 * description - Optional. To be used in the class javadoc section.<br>
 * author - Optional. The author of the source.<br>
 * extends - Optional property to set what this class extends.<br>
 * implements - Optional property to set what this class implements.<br>
 * members - A required string to specify class members. They are a comma delimited string of name=type props.<br>
 * output_path - Optional property to set where the resulting class will go. Default is .<br>
 * <br>
 * <pre>
 * Example:
 * package=com.zitego.util
 * import=java.sql.SQLException,\
 *        com.zitego.sql.DatabaseEntity
 * description=This class bla bla bla...
 * author=John Glorioso
 * extends=DatabaseEntity
 * members=name=String,\
 *         creationUserId=long,\
 *         creationUsername=String
 * output_path=/home/jglorioso/
 * </pre>
 * <br><br>
 * The class will spit out code declaring those properties as protected,pre-pending them
 * with _ and producing getters and setters for each.<br>
 * <br>
 * Usage: java com.zitego.util.ClassBuilder <class name> <props file>
 *
 * @author John Glorioso
 * @version $Id: ClassBuilder.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class ClassBuilder
{
    /** The properties of the class. */
    protected Properties _props = new Properties();
    /** The package the class should be in. */
    protected String _package;
    /** A Vector of imports. */
    protected Vector _imports = new Vector();
    /** The decription of the class. */
    protected String _description;
    /** The author of the class. */
    protected String _author;
    /** The class name. */
    protected String _name;
    /** The class this one extends. */
    protected String _extends;
    /** The Vector of classes that this implements. */
    protected String _implements;
    /** The map of class members. */
    protected TreeMap _members = new TreeMap();
    /** The output file path (without the file name). */
    protected String _outputPath;

    /**
     * To run from the command line.
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length != 2)
        {
            System.out.println("Usage: java com.zitego.util.ClassBuilder <classname> <propsfile>");
            System.exit(1);
        }
        ClassBuilder b = new ClassBuilder(args[0], args[1]);
        b.saveSource();
    }

    /**
     * Creates a new class builder with a class name where all otherproperties
     * need to be set manually.
     *
     * @param String The class name.
     */
    public ClassBuilder(String name)
    {
        _name = name;
    }

    /**
     * Simple constructor with the properties file location
     * and classname to automatically load the properties.
     *
     * @param String The class name.
     * @param String The properties file location.
     */
    public ClassBuilder(String name, String loc) throws IOException
    {
        _name = name;
        loadProperties( new FileInputStream(loc) );
    }

    /**
     * Creates a class builder with a classname and the properties.
     *
     * @param String The class name.
     * @param Properties The properties of the class.
     */
    public ClassBuilder(String name, Properties props)
    {
        _name = name;
        _props = props;
    }

    /**
     * Loads the properties from the given file path.
     *
     * @param FileInputSteam The properties file input stream.
     */
    public void loadProperties(FileInputStream props) throws IOException
    {
        _props.load(props);
        //Set the package
        _package = _props.getProperty("package");
        //Set the imports
        _imports = loadImports();
        //Set the description
        _description = _props.getProperty("description");
        //Set the author
        _author = _props.getProperty("author");
        //Set the extends class
        _extends = _props.getProperty("extends");
        //Set the implements
        _implements = _props.getProperty("implements");
        //Load the members
        _members = loadMembers();
        //Loads the output path
        _outputPath = _props.getProperty("output_path");
    }

    /**
     * Saves the source to disk.
     *
     * @throws IOException if an error occurs.
     */
    public void saveSource() throws IOException
    {
        PrintWriter out = new PrintWriter
        (
            new BufferedWriter
            (
                new FileWriter
                (
                    (
                        _outputPath != null
                            ?
                            _outputPath + (_outputPath.charAt(_outputPath.length()-1) == '/' ? "" : "/")
                            :
                            ""
                    ) + _name + ".java"
                )
            )
        );
        out.print( buildSource() );
        out.flush();
        out.close();
    }

    /**
     * Builds the class source file and returns it as a String.
     *
     * @return String The source code for the class.
     */
    public String buildSource()
    {
        StringBuffer src = new StringBuffer();
        if (_package != null) src.append("package "+_package+";\r\n\r\n");

        //Imports
        int count = _imports.size();
        for (int i=0; i<count; i++)
        {
            src.append("import ").append( _imports.elementAt(i) ).append(";\r\n");
        }
        if (count > 0) src.append("\r\n");

        src.append("/**\r\n");
        if (_description != null)
        {
            TextIndenter indenter = new TextIndenter(1, "* ");
            src.append( indenter.format(TextUtils.wrap(_description, 80)) );
        }
        else
        {
           src.append(" *\r\n");
        }
        src.append(" *\r\n")
           .append( (_author != null ? " * @author " + _author + "\r\n" : "") )
           .append(" * @version $Id: ClassBuilder.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $\r\n")
           .append(" */\r\n")
           .append("public class ").append(_name);
        if (_extends != null) src.append(" extends ").append(_extends);
        if (_implements != null) src.append(" implements ").append(_implements);
        src.append("\r\n{\r\n");

        for (Iterator e=_members.keySet().iterator(); e.hasNext();)
        {
            String prop = (String)e.next();
            if (prop.indexOf("_") == 0) prop = prop.substring(1);
            src.append("    /** The ").append(prop).append(". */\r\n");
            src.append("    private ").append( _members.get(prop) ).append(" _").append(prop).append(";\r\n");
        }

        src.append("\r\n")
           .append("    /**\r\n")
           .append("     * Creates a new ").append(_name).append(".\r\n")
           .append("     */\r\n")
           .append("    public ").append(_name).append("()\r\n")
           .append("    {\r\n")
           .append("\r\n")
           .append("    }\r\n");

        for (Iterator e=_members.keySet().iterator(); e.hasNext();)
        {
            String prop = (String)e.next();
            if (prop.indexOf("_") == 0) prop = prop.substring(1);
            src.append("\r\n")
               .append("    /**\r\n")
               .append("     * Sets the ").append(prop).append(".\r\n")
               .append("     *\r\n")
               .append("     * @param ").append( _members.get(prop) ).append(" The ").append(prop).append(".\r\n")
               .append("     */\r\n")
               .append("    public void set").append( prop.substring(0,1).toUpperCase() ).append( prop.substring(1) )
               .append(             "(").append( _members.get(prop) ).append(" ").append(prop).append(")\r\n")
               .append("    {\r\n")
               .append("        _").append(prop).append(" = ").append(prop).append(";\r\n")
               .append("    }\r\n")
               .append("\r\n")
               .append("    /**\r\n")
               .append("     * Returns the ").append(prop).append(".\r\n")
               .append("     *\r\n")
               .append("     * @return ").append( _members.get(prop) ).append("\r\n")
               .append("     */\r\n")
               .append("    public ").append( _members.get(prop) ).append(" get").append( prop.substring(0,1).toUpperCase() )
               .append(             prop.substring(1) ).append("()\r\n")
               .append("    {\r\n")
               .append("        return _").append(prop).append(";\r\n")
               .append("    }\r\n");
        }
        src.append("}\r\n");
        return src.toString();
    }

    /**
     * Returns the class members as a Hashtable of name to type map.
     *
     * @return TreeMap
     */
    private TreeMap loadMembers()
    {
        TreeMap members = new TreeMap();
        String propsString = _props.getProperty("members");

        if (propsString != null)
        {
            StringTokenizer st = new StringTokenizer(propsString, ",");
            while ( st.hasMoreTokens() )
            {
                String token = st.nextToken().trim();
                int index = token.indexOf("=");
                if (index < 0) throw new RuntimeException("Class member string invalid.");
                members.put( token.substring(0, index), token.substring(index+1) );
            }
        }
        return members;
    }

    /**
     * Returns the imported classes as a vector.
     *
     * @return Vector
     */
    private Vector loadImports()
    {
        Vector imports = new Vector();
        String prop = _props.getProperty("import");
        if (prop != null)
        {
            StringTokenizer st = new StringTokenizer(prop, ",");
            while ( st.hasMoreTokens() )
            {
                imports.add( st.nextToken().trim() );
            }
        }
        return imports;
    }

    /**
     * Sets the name of the class.
     *
     * @param String
     */
    public void setName(String name)
    {
        _name = name;
    }

    /**
     * Returns the name of the class.
     *
     * @return String
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Sets the package for the class.
     *
     * @param String
     */
    public void setPackage(String pkg)
    {
        _package = pkg;
    }

    /**
     * Returns the package for the class.
     *
     * @return String
     */
    public String getPackage()
    {
        return _package;
    }

    /**
     * Sets the members for the class.
     *
     * @param TreeMap
     */
    public void setMembers(TreeMap m)
    {
        if (m == null) _members = new TreeMap();
        else _members = m;
    }

    /**
     * Returns the members of the class.
     *
     * @return TreeMap
     */
    public TreeMap getMembers()
    {
        return _members;
    }

    /**
     * Sets the imports for the class.
     *
     * @param Vector
     */
    public void setImports(Vector i)
    {
        if (i == null) _imports = new Vector();
        else _imports = i;
    }

    /**
     * Returns the imports of the class.
     *
     * @return Vector
     */
    public Vector getImports()
    {
        return _imports;
    }

    /**
     * Sets the extends for the class.
     *
     * @param String
     */
    public void setExtends(String ext)
    {
        _extends = ext;
    }

    /**
     * Returns the extends for the class.
     *
     * @return String
     */
    public String getExtends()
    {
        return _extends;
    }

    /**
     * Sets the implements for the class.
     *
     * @param String
     */
    public void setImplements(String i)
    {
        _implements = i;
    }

    /**
     * Returns the implements for the class.
     *
     * @return String
     */
    public String getImplements()
    {
        return _implements;
    }

    /**
     * Sets the output path for the class.
     *
     * @param String
     */
    public void setOutputPath(String path)
    {
        _outputPath = path;
    }

    /**
     * Returns the output path for the class.
     *
     * @return String
     */
    public String getOutputPath()
    {
        return _outputPath;
    }
}
