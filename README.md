Hi there, thanks for looking up my plugin!

This plugin's functionality is focussed on the binary APK
upload. If you'd like to contribute more behaviours then...

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

## Getting started

1. Create an Amazon Security Profile by following the instructions [here](https://developer.amazon.com/docs/app-submission-api/auth.html)
    - Then create a Security Profile json file like the following:
      ```json
      {
          "grant_type": "client_credentials",
          "client_id": "amzn1.application-oa2-client.ae941846cdd745e9a53319f7bb98d435",
          "client_secret": "41d135b2b02ce5f2fbf7643a66477c089fcc1d88d11f69d3e4a6285b917ca35d",
          "scope": "appstore::apps:readwrite"
      }
      ```
2. Read the Amazon Publishing API overview [here](https://developer.amazon.com/docs/app-submission-api/overview.html)
3. Add the plugin to your project following the instructions [here](https://plugins.gradle.org/plugin/app.brant.amazonappstorepublisher)
4. Add the `amazon { }` closure to the module that creates your APK and specify the following attributes:
    ```groovy
    amazon {
        securityProfile = file("<path-to-security-profile.json>")
        applicationId = "<applicationId>"
        pathToApks = [ file("<path-to-apk>") ]
        replaceEdit = true // true if you want to delete any existing Edit ("Upcoming version")
        replaceApks = true // true if you want to replace existing apks in an Edit ("Upcoming version")
    }
    ```
    All paths are relative.
    
5. Run `gradlew publishToAmazonAppStore`
    - For large files or slow networks, you might need to increase the read and write timeouts in seconds by setting `app.brant.amazonappstorepublisher.PublishPlugin.writeTimeout` or `app.brant.amazonappstorepublisher.PublishPlugin.readTimeout` jvm properties
        - e.g.
          ```bash
          gradlew -Dapp.brant.amazonappstorepublisher.PublishPlugin.writeTimeout=600 -Dapp.brant.amazonappstorepublisher.PublishPlugin.readTimeout=300 publishToAmazonAppStore
          ```
6. Add it to your Continuous Deployment pipeline

