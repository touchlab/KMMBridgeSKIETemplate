// swift-tools-version:5.3
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://maven.pkg.github.com/touchlab/KMMBridgeSKIETemplate/co/touchlab/kmmbridgekickstartskie/allshared-kmmbridge/0.1.5/allshared-kmmbridge-0.1.5.zip"
let remoteKotlinChecksum = "338cc5ea733cd9ffdb5fb66dbd3d4fe3f33c9003f982b731c525b4d242a04b6c"
let packageName = "allshared"
// END KMMBRIDGE BLOCK

let package = Package(
    name: packageName,
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: packageName,
            targets: [packageName]
        ),
    ],
    targets: [
        .binaryTarget(
            name: packageName,
            url: remoteKotlinUrl,
            checksum: remoteKotlinChecksum
        )
        ,
    ]
)