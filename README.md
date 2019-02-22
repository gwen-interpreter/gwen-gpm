[![Gwen-web](https://user-images.githubusercontent.com/1369994/29352618-c4cbb7b0-82aa-11e7-9f84-07d1a4a1e9ac.png)](https://github.com/gwen-interpreter/gwen/wiki/The-Gwen-Logo)

Gwen-GPM
========

A package manager for downloading and installing [Gwen](https://github.com/gwen-interpreter/gwen)
[packages](https://github.com/gwen-interpreter/gwen#what-engines-are-available) and their binary dependencies to local
workstations, servers, or shared workspaces in a consistent manner across platforms. Downloaded packages are cached and
managed in a local `.gwen/cache` folder in your user home directory and can be installed to the file system in
locations that you specify. Checksum verifications are performed on all downloaded packages to verify their integrity
and no environment variables or system paths are created or modified during installation so your system remains intact.

The download and installation of the following packages are managed:

- Gwen packages
  - [gwen-web](https://github.com/gwen-interpreter/gwen-web)
- Native web drivers
  - [chrome-driver](https://sites.google.com/a/chromium.org/chromedriver/)
  - [gecko-driver](https://github.com/mozilla/geckodriver)
  - [ie-driver](https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver)
- Java libraries
  - [selenium](https://github.com/SeleniumHQ/selenium)

### Current Status

[![Build Status](https://travis-ci.org/gwen-interpreter/gwen-gpm.svg?branch=master)](https://travis-ci.org/gwen-interpreter/gwen-gpm)

- [Latest release](https://github.com/gwen-interpreter/gwen-gpm/releases/latest)
- [Change log](CHANGELOG)

### Benefits

Benefits of using this package manager when working with Gwen include:

- All downloads and installs are managed for you
- You can easily update to the latest versions of packages
- You can easily switch between versions of packages
- All downloads are verified for integrity before they are installed
- Installation is consistent across platforms

Runtime Requirements
--------------------

- Java SE 8 Runtime

Installation
------------

- Ensure that you have Java 8 or higher installed. 
  - You can verify what version of Java you have by typing `java -version` in a command prompt
  - If the command does not report Java version 8 or higher, then install the latest [Java (JRE)](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
- Download and unpack the [latest gwen-gpm zip](https://github.com/gwen-interpreter/gwen-gpm/releases/latest) to a location 
  on your drive
- Add the bin folder that resides in your unpacked location to your system path
- If on Linux/Mac, run `chmod -R u+x .` in the root of your install directory to enable execution permissions
- Configure a [proxy connection](#proxy-connections) if you are behind a firewall

Usage
-----

```
gwen-gpm [options] <operation> <package> <version> [destination]

  -p, --properties <files>
                           Comma separated list of properties files
  <operation>              install | update
  <package>                gwen-web | chrome-driver | gecko-driver | ie-driver | selenium
  <version>                latest | version property | version number
  [destination]            the destination folder to install the package to
                           - if not specified, defaults to ~/.gwen/packages/<package>
```

### Install Operations

Each supported package is a Zip or Tar.Gz file that can be downloaded and installed (unpacked) to a directory. The
package manager will know which type of archive to download and install for the platform you are on. Installation
involves downloading a package archive (and caching it if it has not been downloaded already) and then unpacking it to
the default ~/.gwen/packages/package-name folder or specified destination folder. Checksum verifications are performed on all
downloads and re-verified on each install. Subsequent installs of the same version of a package will install the cached
package instead of downloading it again. In the case of linux environments, execution permissions will be assigned to
all extracted files at installation time. Any package installations that exist in a target directory are deleted
before new installations are performed.

#### Examples:

- Install the latest gwen-web to the default directory at ~/.gwen/packages/gwen-web
  - `gwen-gpm install gwen-web latest`

- Install the latest gwen-web to the target/gwen-packages/gwen-web folder relative to current directory
  - `gwen-gpm install gwen-web latest target/gwen-packages/gwen-web`

- Install a specific version of gecko driver to the default directory at ~/.gwen/packages/gecko-driver
  - `gwen-gpm install gecko-driver 0.18.0`

- Install a specific version of gecko driver to c:/web-drivers/gecko-driver.
  - `gwen-gpm install gecko-driver 0.18.0 c:/web-drivers/gecko-driver`

- Install the chrome driver version set in a property to the target/chrome-driver folder relative to current directory
  - `gwen-gpm install chrome-driver chrome.version target/chrome-driver -p path/to/file.properties`

### Update Operations

When using the `install` operation in conjunction with the `latest` version literal, the package manager will fetch
and resolve the latest version of the specified package from the internet on the first such call and cache it. All
subsequent installs of that same latest package will then resolve to this cached value instead of being fetched from
the internet again. This makes the package manager efficient and is fine if you're not interested in updating to later
releases that could have been published in the meantime. But if you do want to update to the absolute latest, then you
can use the `update` operation instead of `install`. Every time you use `update`, the package manager will re-fetch
the latest published version of the package from the internet and update its cached version to that value before
installing that package. Subsequent installs of latest packcages will then resolve to that new latest version in the
cache.

How often you perform updates is up to you, and you can do it frequently or every time if performance overhead is
not a concern. Update operations behave the same way as first time install operations do in regards to 'latest'
versions. If on the other hand you specify a specific version, then updates will behave exactly like installs do every
time and so it won't matter which one you use.

#### Examples:

- Update latest gwen-web version in cache and install it to the default directory at ~/.gwen/packages/gwen-web
  directory
  - `gwen-gpm update gwen-web latest`

- Update latest gwen-web version in cache and install it to the target/gwen-packages/gwen-web folder relative to current
  directory
  - `gwen-gpm update gwen-web latest target/gwen-packages/gwen-web`

- Install a specific version of gecko driver to the default directory at ~/.gwen/packages/gecko-driver (update behaves
  the same as install when a specific version is specified)
  - `gwen-gpm update gecko-driver 0.18.0`

- Install a specific version of gecko driver to the c:/web-drivers/gecko-driver folder location (update behaves the
  same as install when a specific version is specified)
  - `gwen-gpm update gecko-driver 0.18.0 c:/web-drivers/gecko-driver`

- Update/install the chrome driver version set in a property to the target/chrome-driver folder relative to current
  directory. If the version in the properties file is set to 'latest' then the latest version is updated in the cache 
  and installed. Otherwise if the version is set to a specific release, then the update will behave like an install.
  - `gwen-gpm update chrome-driver chrome.version target/chrome-driver -p path/to/file.properties`

Package Verification
--------------------

SHA-256 checksums are used to verify the integrity of all downloaded packages.

Proxy Connections
-----------------

If you need to go through a proxy for downloads, create a `.gwen/gwen-gpm.properties` file in your home directory
(Mac home: /Users/<your-username>, Windows home: C:\Users\<your-username>) with the following
settings:

- `gwen.proxy.host=<your proxy host>`
- `gwen.proxy.port=<your procy port>`

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

Copyright 2017-2018 Branko Juric, Brady Wood

This software is open sourced under the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

See also: [LICENSE](LICENSE).

This project has dependencies on other open source projects. All distributed third party dependencies and
their licenses are listed in the [LICENSE-THIRDPARTY](LICENSE-THIRDPARTY) file.

Open sourced 16 August 2017 4:51 pm AEST
