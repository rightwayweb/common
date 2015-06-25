RELEASE NOTES:

Dependencies: jasper-runtime.jar, activation.jar, mail.jar, servlet.jar

1.1.1  - ImageInfo bug fix to close connections on check.
       - Implemented non-cached GroupNavigator.
       - Added smtp port to mail properties.

1.1    - Added ability to pool db connections with DBCP pooling.
       - Added new PreparedStatement implementation.
       - Added FTPUtils.
       - Added flag to encode or not to GetData.
       - Smoother and more efficient image scaling in ImageUtils.
       - Made NameValueObject Serializable
       - Fixed AutoLogin to en/decode entire cookie value.

1.0.9b - Fix of connection errors on high load.

1.0.9a - Fix to DataSet to add sort columns at end instead of beginning of list.
       - Added envelope from address to SMTPMail.
       - Close in FileUtils.getFileContents

1.0.9  - Added ImageUtils.
       - Bug fix to prevent infinite recursion from toString in InformationEntity.

1.0.8  - Added sql server database handle.
       - Made inactive status = 0

1.0.7a - Added domain information to cookie utils.

1.0.7  - Added headers and reply to address to SMTPMail.

1.0.6  - Added FIREFOX browser type to UserAgent.
       - Bug fix for ConnectionFactory on parsing database urls. Now allows arguments in url
       - Added replaceHighAscii in TextUtils
       - Added auto flush property to FileHandler.

1.0.5c - Added decodeToByteArray to com.zitego.util.Hex.

1.0.5b - Added StatusType evaluation to return INACTIVE if evaluate results in null.

1.0.5a - Made removeEndCharacters, removeLeadingCharacter, removeLeadingCharacters,
         removeTrailingCharacter, and removeTrailingCharacters return a
         StringBuffer of the characters removed.
       - Update to BooleanFormat to update the ParsePosition in parseObject.
       - Update to TextIndenter to update the ParsePosition in parseObject.

1.0.5  - Replaced the internal telnet protocol handler with one built and supported by
         the apache commons project. Methods inputfeed, startup, reset, and sendTelnetControl
         have been removed as they are not in use.

1.0.4f - Added UniqueNameDatabaseEntity.
       - Changed BooleanFormat to use other objects besides just boolean.
       - Added MySQLColumnType

1.0.4e - Attempted fix for read locks in TelnetProtocolHandler.

1.0.4d - Added getCookies to URLContentReader.
       - Fixed set query string bug in GetData
       - Created DatabaseUpdater and changed the DatabaseEntity class to implement it.
       - Added DESPasswordEncryption
       - Telnet bug fix

1.0.4c - Added com.zitego.http.AutoLogin

1.0.4b - Fixed InformationEntity to show all getters.

1.0.4a - Added RandomPasswordGenerator
       - Added Base64DecoderStream to use in zitego mail instead of sun's. Sun's sucks.

1.0.4  - Bug fix in DataSet and DataSetCollection to default sort null to descending.
       - Changed BaseConfigServlet.ConfigChecker.addTask to use getMethod instead
         of getDeclaredMethod. Also had to make the reload() method public.
         Also changed BaseConfigServlet.resetStaticProperties to reload the
         properties file.
       - Removed the createJsSourceFile() method from StringValidation leaving
         only the one where you must specify a path to write to.
       - Changed ConnectionFactory to only store a DBConfig in StaticProperties
         if no PropertyStore is given.
       - Added PropertyStore.

1.0.3  - Removed markup and web specific packages to their own projects.

1.0.2  - Re-worked the UrlContentReader (formerly in util) to a new more robust
         http package.
       - Changed the parse method in the MarkupConverter interface to parse
         Objects as well as Strings. Changed all implementing classes.
       - Added third party api classes.
       - Added CookieUtils.java, DateFilter.java, GenericId.java, ImageInfo.java,
         InformationEntity.java, NonFatalExceptionWithJavascript.java
       - Changed BaseServlet handleError to check for a NonFatalWebappException
         to get the forward page from before calling getErrorPage
       - Added multiple com.zitego.markup.xml classes for parsing xml
       - Added discussion for message boarding

1.0.1  - Added DateRange, DateRangeType, DateRangeFactory, ObjectFormatter,
         NameValueObject, ClassBuilder, XmlUtils, DropDown, ConstantDropDown,
         DatabaseDropDown, CachedDatabaseDropDown, Domain, DomainAPI,
         DomainAPIException, DomainManager, DomainNotOwnedException,
         InvalidDomainException, WhoIs, EnomDomainAPI, and EnomRRPCode classes
         to util.
       - Added a repeat method to TextUtils.
       - Changed the leading characters to a String in TextIndenter.
       - Added a way to set the DBConfig in StaticProperties by a variable name
         in ConnectionFactory.
       - Added ability to set JSEventHandlers by an AttributeList in MarkupTag.
       - Added ability to supply a list of methods to call on an object in
         FormatTag.

1.0    - Initial Release
