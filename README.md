[![Gwen-web](https://github.com/gwen-interpreter/gwen/wiki/img/gwen-attractor.png)](https://github.com/gwen-interpreter/gwen/wiki/The-Gwen-Logo)

Gwen Package Manager
====================

A cross platform package manager for downloading and installing [Gwen](https://github.com/gwen-interpreter/gwen)
[packages](https://github.com/gwen-interpreter/gwen#what-engines-are-available) and associated binaries and native
web drivers to local workstations, servers, or shared workspaces. Downloaded packages are cached and managed in a
local `.gwen/cache` folder in your user home directory and can be installed to any location of your choosing. Checksum
verifications are performed on all downloaded packages and no environment variables or system paths are created or
modified during installation so your system remains in tact.

The following packages are currently managed:

- [gwen-web](https://github.com/gwen-interpreter/gwen-web)
- [chrome-driver](https://sites.google.com/a/chromium.org/chromedriver/)
- [gecko-driver](https://github.com/mozilla/geckodriver)
- [ie-driver](https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver)
- [selenium](https://github.com/SeleniumHQ/selenium)

### Current Status

[![Build Status](https://travis-ci.org/gwen-interpreter/gwen-gpm.svg?branch=master)](https://travis-ci.org/gwen-interpreter/gwen-gpm)

- [Latest release](https://github.com/gwen-interpreter/gwen-gpm/releases/latest)
- [Change log](CHANGELOG)

### Benefits

Benefits of using this package manager when working with Gwen include:

- Package downloads and installs are managed for you
- Easily update to the latest versions of packages
- Easily switch between versions of packages
- All downloads are verified for integrity before they are installed
- Installation is consistent across platforms

Runtime Requirements
--------------------

- Java SE 8 Runtime

Installation
------------
- Download and unpack the [latest](https://github.com/gwen-interpreter/gwen-gpm/releases/latest) zip distribution to a
  location on your drive
- Add the bin folder that resides in your unpacked location to your system path
- If on Linux/Mac, run `chmod -R u+x .` in the root of your install directory to enable execution permissions
- Download the gwen-gpm.properties file and save it into the root of your user home directory

Usage
-----

```
gwen-gpm [options] <operation> <package> <version> [<destination>]

  -p, --properties <files>
                           Comma separated list of properties files
  <operation>              install | update
  <package>                gwen-web | chrome-driver | gecko-driver | ie-driver | selenium
  <version>                latest | version property | version number
  <destination>            the destination folder to install the package to
```

### Install Operations

Each supported package is a Zip or Tar.Gz file that can be downloaded and installed (unpacked) to a directory. The
package manager will know which type of archive to download and install for the platform you are on. Installation
involves downloading a package archive (and caching it if it has not been downloaded already) and then unpacking it to
the specified destination folder. Checksum verifications are performed on all downloads and re-verified on each
install. Subsequent installs of the same version of a package will install the cached package instead of downloading
it again. In the case of linux environments, execution permissions will be assigned to all extracted files at
installation time. Any file system contents that may exist in a target directory are zipped and backed up to the
local `.gwen/backukps` folder in your user home directory before installation takes place (so you can manually recover
them in the event of an inadvertent overwriting install if need be).

#### Examples:

Install the latest gwen-web to the target/gwen-packages/gwen-web folder relative to current directory

`gwen-gpm install gwen-web latest target/gwen-packages/gwen-web`

Install a specific version of gecko driver to c:/web-drivers/gecko-driver.

`gwen-gpm install gecko-driver 0.18.0 c:/web-drivers/gecko-driver`

Install the chrome driver version set in a property to the target/chrome-driver folder relative to current directory

`gwen-gpm -p path/to/file.properties install chrome-driver chrome.version.property target/chrome-driver`

### Update Operations

When using the `install` operation in conjunction with the `latest` version literal, the package manager will fetch
and resolve the latest version of the specified package from the internet on the first such call and cache it. All
subsequent installs of that same latest package will then resolve to this cached value instead of being fetched from
the internet again. This makes the package manager efficient and is fine if you're not interested in updating to later
releases that could have been published in the meantime. But if you do want to update to the absolute latest, then you
can use the `update` operation instead of `install`. Every time you use `update`, the package manager will re-fetch
the latest published version of the package from the internet and update its cached version to that value before
installing that package. Subsequent installs will then resolve that new latest version in the cache.

How often you perform updates is up to you, and you can do it frequently or every time if performance overhead is
is not a concern. Update operations behave the same way as first time install operations do in regards to 'latest'
versions. If on the other hand you specify a specific version, then updates will behave exactly like installs do every
time and so it won't matter which one you use.

#### Examples:

Update latest gwen-web version in cache and install it to the target/gwen-packages/gwen-web folder relative to current
directory

`gwen-gpm update gwen-web latest target/gwen-packages/gwen-web`

Install a specific version of gecko driver to the c:/web-drivers/gecko-driver folder location (update behaves the
same as install when a specific version is specified)

`gwen-gpm update gecko-driver 0.18.0 c:/web-drivers/gecko-driver`

Update/install the chrome driver version set in a property to the target/chrome-driver folder relative to current
directory. If the version in the properties file is set to 'latest' then the latest version is updated in the cache
and installed. Otherwise if the version is set to a specific release, then the update will behave like an install.

`gwen-gpm -p path/to/file.properties update chrome-driver chrome.version.property target/chrome-driver`

Package Verification
--------------------

SHA-256 checksums are used to verify the integrity of all downloaded packages to help prevent MITM attacks and are
necessary since all packages are downloaded from public web sites where releases are published. We maintain them
regularly in this [gwen-gpm.properties](???) file that you will have downloaded during your
[gwen-pgm installation](#installation). If you need to add a new checksum entry for a later package version that is
not listed in your copy of this file, you can do so by calculating its checksum value with the following command
against a known distribution before adding it the file.

   (Linux, OSX, or Windows via Cygwin): `shasum -a 256 known-dist.zip`

After running the above command, you can assign the printed checksum value to a property in the gwen-gpm.properties
file on your system like so:
  - `gwen.checksum.package-version=checksum`
    - Where:
      - `package` = gwen-web | chrome-driver | gecko-driver | ie-driver | selenium
      - `version` = the package version number
      - `checksum` = the sha-256 checksum value (hex digest) as calculated above
        - Note: you can also provide a comma separated list of checksums for different cross platform package types

Contributions
-------------

New capabilities, improvements, and fixes are all good candidates for contribution. Submissions can be made using
pull requests. Each submission is reviewed and tested before being integrated and released to the community.

By submitting contributions, you agree to release your work under the license that covers this software.

How to contribute:
1. Fork this repository
2. Create a branch on your fork
3. Commit your changes to your branch
4. Push your branch to your fork
5. Create a pull request from your branch to here

License
-------

Copyright 2017 Branko Juric, Brady Wood

This software is open sourced under the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

See also: [LICENSE](LICENSE).

This project has dependencies on other open source projects. All distributed third party dependencies and
their licenses are listed in the [LICENSE-THIRDPARTY](LICENSE-THIRDPARTY) file.

Open sourced dd August 2017 hh:mm pm AEST
