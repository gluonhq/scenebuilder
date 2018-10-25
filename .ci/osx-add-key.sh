#!/bin/sh

KEY_CHAIN=ios-build.keychain

# Create a custom keychain
security create-keychain -p travis $KEY_CHAIN

# Make the custom keychain default, so xcodebuild will use it for signing
security default-keychain -s $KEY_CHAIN

# Unlock the keychain
security unlock-keychain -p travis $KEY_CHAIN

# Set keychain timeout to 1 hour for long builds
# see http://www.egeek.me/2013/02/23/jenkins-and-xcode-user-interaction-is-not-allowed/
security set-keychain-settings -t 3600 -l ~/Library/Keychains/$KEY_CHAIN

# Add certificates to keychain and allow codesign to access them
security import ./app/assets/osx/apple.cer -k ~/Library/Keychains/$KEY_CHAIN -T /usr/bin/codesign
security import ./app/assets/osx/codesign.cer -k ~/Library/Keychains/$KEY_CHAIN -T /usr/bin/codesign
security import ./app/assets/osx/codesign.p12 -k ~/Library/Keychains/$KEY_CHAIN -P $KEY_SECRET -T /usr/bin/codesign

security set-key-partition-list -S apple-tool:,apple: -s -k travis $KEY_CHAIN
