# Generating certificate for development

Jetty documentation: <http://www.eclipse.org/jetty/documentation/current/configuring-ssl.html>
    
## Development    

Create development keystore and certificate:

    keytool -keystore config/dev.keystore -storepass wikivents -keypass wikivents -alias jetty -genkey -keyalg RSA -dname "CN=localhost, OU=Development, O=Wikivents, L=Groningen, ST=GN, C=NL" -validity 365   
   
## Configure SSL in local.properties

Add these configuration options to config/local.properties: _(example values are working for localhost)_

    http.sslEnabled=true
    http.sslKeyStorePath=config/dev.keystore
    http.sslKeyStorePassword=wikivents
    http.sslKeyManagerPassword=wikivents
    http.sslTrustStorePath=config/dev.keystore
    http.sslTrustStorePassword=wikivents


# Reference

## Keytool -genkey options    	

    -alias <alias>                  alias name of the entry to process
    -keyalg <keyalg>                key algorithm name
    -keysize <keysize>              key bit size
    -sigalg <sigalg>                signature algorithm name
    -destalias <destalias>          destination alias
    -dname <dname>                  distinguished name
    -startdate <startdate>          certificate validity start date/time
    -ext <value>                    X.509 extension
    -validity <valDays>             validity number of days
    -keypass <arg>                  key password
    -keystore <keystore>            keystore name
    -storepass <arg>                keystore password
    -storetype <storetype>          keystore type
    -providername <providername>    provider name
    -providerclass <providerclass>  provider class name
    -providerarg <arg>              provider argument
    -providerpath <pathlist>        provider classpath
    -v                              verbose output
    -protected                      password through protected mechanism

