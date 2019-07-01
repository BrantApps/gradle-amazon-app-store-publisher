Hi there, thanks for looking up my plugin!

This plugin's functionality is focussed on the binary APK
upload. If you'd like to contribute more behaviours then...

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

## Getting started

1. Create an Amazon Security Profile by following the instructions [here](https://developer.amazon.com/docs/app-submission-api/auth.html)
2. Read the Amazon Publishing API overview [here](https://developer.amazon.com/docs/app-submission-api/overview.html)
3. Add the plugin to your project following the instructions [here](https://plugins.gradle.org/plugin/app.brant.amazonappstorepublisher)
4. Add the `amazon { }` closure to the module that creates your APK and specify the following attributes;
    ```
    amazon {
        securityProfile = file("<path-to-security-profile.json>")
        applicationId = "<applicationId>"
        pathToApks = [ file("<path-to-apk>") ]
        replaceEdit = true
    }
    ```
    All paths are relative.
    
5. Run `gradlew publishToAmazonAppStore`
6. Add it to your Continuous Deployment pipeline

