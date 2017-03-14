#!/bin/bash
set -o nounset
set -o errexit
VERSION=`python -c "from xml.dom.minidom import parse;dom = parse('pom.xml');print [n.firstChild for n in dom.getElementsByTagName('version') if n.parentNode == dom.childNodes[0]][0].toxml()"`
echo "GST Site Foundation version $VERSION packager"

execLocation="$PWD"

if [[ $(uname -s | tr '[:lower:]' '[:upper:]') = *CYGWIN* ]]; 
then
	tmpBase="c:\tmp\gsf-deploy"
	echo
	echo "CygWin DETECTED!"
	echo "Will use temporary folder: $tmpBase"
	echo
else
	tmpBase=/tmp/gsf-deploy
	echo
	echo "CygWin not detected... running on Linux, Mac or some other Linux distro"
	echo "Will use temporary folder: $tmpBase"
	echo
fi

mavenOutputLog=$tmpBase/mvn-gsf-$VERSION.out

tmpLocation=$tmpBase/gsf-$VERSION

siteLocation=$tmpLocation/site

kitLocation=$tmpLocation/kit

trap onexit ERR


function onexit() {
    local exit_status=${1:-$?}
    echo
    echo
    echo "An error occured. In many case this is caused by missing artifacts from the local maven repository."
    echo "Try running"
    echo '(cd gsf-build-tools && mvn -q -Dmaven.test.skip=true install && cd .. && mvn install && mvn -P '\''!samples'\'' site)'
    echo "mvn -o site:stage -P '!samples'" -DstagingDirectory=""$siteLocation""
    echo "and then try to run package.sh again."
    echo "the output of the failed build is probably in $mavenOutputLog"
    exit $exit_status
}

function buildJARs() {
	echo "[$(date)] Downloading all artifacts"
	mvn -q dependency:go-offline | awk '{ print "[DOWNLOADING ARTIFACTS] ", $0; }' > $mavenOutputLog

	echo "[$(date)] Building GSF jars"
	mvn -o clean install | awk '{ print "[BUILDING JARS] ", $0; }' >> $mavenOutputLog

	echo "[$(date)] Building GSF sample"
	(cd gsf-sample && mvn -o clean install | awk '{ print "[BUILDING SAMPLE] ", $0; }') >> $mavenOutputLog

	echo "[$(date)] GSF jars successfully built !"
}

function packageKit() {
	echo "[$(date)] Packaging GSF Kit"

	echo "[$(date)]   initializing 'kit' folder $kitLocation"
	if [ ! -d $kitLocation ] ;
	then
        	mkdir $kitLocation
	fi

	echo "[$(date)]   copying JAR files with compiled classes inside $kitLocation"
	cp gsf-core/target/gsf-core-$VERSION.jar $kitLocation
	cp gsf-legacy/target/gsf-legacy-$VERSION.jar $kitLocation

	echo "[$(date)]   copying JavaDoc and Source Files inside $kitLocation"
	cp gsf-core/target/gsf-core-$VERSION-javadoc.jar $kitLocation
	cp gsf-core/target/gsf-core-$VERSION-sources.jar $kitLocation
	cp gsf-legacy/target/gsf-legacy-$VERSION-javadoc.jar $kitLocation
	cp gsf-legacy/target/gsf-legacy-$VERSION-sources.jar $kitLocation

	echo "[$(date)]   copying README.md inside $kitLocation"
	cp ./README.md $kitLocation

	#mkdir "$tmpLocation/gsf-sample/"
	#cp -R gsf-sample/src "$tmpLocation/gsf-sample/"
	#cp -R gsf-sample/resources "$tmpLocation/gsf-sample/"

	echo "[$(date)] Adding license to $kitLocation"
	cp LICENSE "$kitLocation"

	echo "[$(date)]   compressing kit"
	if [ ! -d `pwd`/target ] ; then mkdir `pwd`/target ;fi
	kitArchive=`pwd`/target/gsf-$VERSION-kit
	cd $tmpLocation
	tar -czf ${kitArchive}.tgz kit
	zip -q -r ${kitArchive}.zip kit

	echo "[$(date)] GSF Kit packaging complete."

	echo "Kits are ready for pick-up here:"
	echo "  ${kitArchive}.tgz"
	echo "  ${kitArchive}.zip"
	echo

	cd $execLocation
}

function packageWebsite() {
	echo "[$(date)] Building GSF website"

	echo "[$(date)]   preparing site"
	mvn -P '!samples' site | awk '{ print "[PREPARING SITE] ", $0; }' >> $mavenOutputLog

	echo "[$(date)]   aggregating javadoc"
    mvn -P '!samples' javadoc:aggregate | awk '{ print "[AGGREGATING JAVADOC] ", $0; }' >> $mavenOutputLog

	echo "[$(date)]   staging site under $siteLocation"
	#mvn site:stage -P '!samples' -DstagingDirectory=$siteLocation > /dev/null
	mvn site:stage -P '!samples' -DstagingDirectory=$siteLocation | awk '{ print "[STAGING SITE] ", $0; }' >> $mavenOutputLog

	echo "[$(date)]   initializing 'downloads' folder $siteLocation/downloads"
	if [ ! -d $siteLocation/downloads ] ;
	then
	        mkdir $siteLocation/downloads
	fi

	echo "[$(date)]   copying JAR files inside $siteLocation/downloads"
	cp gsf-core/target/gsf-core-$VERSION.jar $siteLocation/downloads/
	cp gsf-legacy/target/gsf-legacy-$VERSION.jar $siteLocation/downloads/

	echo "[$(date)]   copying JavaDoc and Source Files inside $siteLocation/downloads"
	cp gsf-core/target/gsf-core-$VERSION-javadoc.jar $siteLocation/downloads/
	cp gsf-core/target/gsf-core-$VERSION-sources.jar $siteLocation/downloads/
	cp gsf-legacy/target/gsf-legacy-$VERSION-javadoc.jar $siteLocation/downloads/
	cp gsf-legacy/target/gsf-legacy-$VERSION-sources.jar $siteLocation/downloads/

	echo "[$(date)] Adding module sites to $siteLocation"
	cp -R gsf-core/target/site "$siteLocation/gsf-core/"
	cp -R gsf-legacy/target/site "$siteLocation/gsf-legacy/"

	echo "[$(date)] Adding license to $siteLocation"
	cp LICENSE "$siteLocation"

	echo "[$(date)] Compressing GSF website"
	if [ ! -d `pwd`/target ] ; then mkdir `pwd`/target ;fi
	websiteArchive=`pwd`/target/gsf-$VERSION-website
	cd $tmpLocation
	tar -czf ${websiteArchive}.tgz site
	zip -q -r ${websiteArchive}.zip site

	echo "[$(date)] GSF's website is ready for pick-up here:"
	echo "[$(date)]   ${websiteArchive}.tgz"
	echo "[$(date)]   ${websiteArchive}.zip"
	echo

	cd $execLocation
}

function buildAndPackageAll() {
	buildJARs 
	packageWebsite
	packageKit
}

if [ ! -d $tmpBase ] ;
then
	mkdir $tmpBase
fi

if [ ! -d $HOME/.m2/repository/com/fatwire/gst/gst-foundation -o ! -d $HOME/.m2/repository/tools/gsf ] ;
then
   echo "[$(date)] The GSF artifacts are not present in your maven repository. This is expected if you are building for the first time on this computer."
   echo "[$(date)] Starting initial build"
   echo
   # first install the build tools
   # then run install on the whole kit to force-download all dependencies (even ones not caught by dependency:go-offline)
   (cd gsf-build-tools && mvn -q -Dmaven.test.skip=true clean install && cd ..)
   mvn -q install > $mavenOutputLog
   echo "[$(date)] Finished initial build"
fi

echo "[$(date)] Clearing $tmpLocation"
if [ -d "$tmpLocation" ] ; then rm -Rf "$tmpLocation" ;fi
mkdir -p "$tmpLocation"

commandName=${1:-}
if [[ -z "$commandName" ]]; then
    commandName="all"
fi

case ${commandName} in
	"jar")	buildJARs ;;
	"kit")	packageKit ;;
	"site")	packageWebsite ;;
	"all")	buildAndPackageAll ;;
	*)	echo UNSUPPORTED OPERATION "$commandName". ; exit ;;
esac



# notes for GSF release engineer:

# to get a graphical dependency tree: http://www.summa-tech.com/blog/2011/04/12/a-visual-maven-dependency-tree-view/
#  mvn org.apache.maven.plugins:maven-dependency-plugin:2.5:tree -DoutputType=graphml -DoutputFile=dependency.graphml
#   yEd ./dependency.graphml 
# find . -name dependency.graphml -exec rm {} \;


# find newer versions for plugins
#mvn versions:display-plugin-updates

#versions:dependency-updates-report
#  Generates a report of available updates for the dependencies of a project.

#versions:display-dependency-updates
#  Displays all dependencies that have newer versions available.

#dependency:list-repositories


# Handing license header files

# The license is defined in the gsf-build-tools project. If the header needs to change it needs to be done there
# The LicenseHeader.txt does not contain the copyright statement, as this can be different for each file. This file is used
# to trick the license plugin and not barf over different copyright statements. 
# you can issue mvn license:format -Dcompany="My Corporation" to add header files with a copyright statement to all files that do not have a license header.

# quick check if a copyright statement is present
# find . -name '*.java'  ! -exec egrep -q 'Copyright .* All Rights Reserved' {} \; -ls


#interacting with the webdav server

#downlaod
#wget --user=someone@oracle.com --ask-password https://stbeehive.oracle.com/content/dav/st/WebCenter%20Sites%20GSF/Documents/gst-foundation-11.6.0-SNAPSHOT.tgz


# upload
#curl -T target/gst-foundation-11.6.0-SNAPSHOT.tgz  --user someone@oracle.com https://stbeehive.oracle.com/content/dav/st/WebCenter%20Sites%20GSF/Documents/gst-foundation-11.6.0-SNAPSHOT.tgz

# Instruction to add to change log
# git log e7c4aab67d0185c4872e53b75835642cd5bedb51..HEAD > /tmp/git-log
# sed -i  /^commit/d /tmp/git-log 
# sed -i  '/<dolf.dijkstra@xs4all.nl>//g' /tmp/git-log 
# sed -i  's/<dolf.dijkstra@xs4all.nl>//g' /tmp/git-log 
# sed -i  's/<tony.field@metastratus.com>//g' /tmp/git-log 
# sed -i  's/<david.chesebro@metastratus.com>//g' /tmp/git-log 
# vi src/site/apt/changes-11g.apt 

# Site Update instructions (tested for version 12.0.1 on Oct 24, 2016 by Tony Field)
# - update poms to new release version for all modules (gst-foundation, gsf-build-tools, gsf-core, gsf-legacy)
# - update site.xml to link to new "previous version" site
# - update the download.apt.vm page to link to the new download and update prior versions
# - update documentation as needed
# - run this script to build the package (sh package.sh)
# - validate build
# - commit poms & site
# - issue pull request into version trunk (e.g. gst-foundation-12)
# - save full site zip from target folder
# - checkout gh-pages branch
# - add new folder to releases directory for new version (e.g. gsf-12.0.1)
# - extract full site zip from gh-pages branch
# - place content from teh site folder into the release folder in the gh-pages checkout
# - edit index.html to redirect users to the new version (i.e. update the meta refresh tag)
# - commit the gh-pages changes
# - verify the site - navigate to gst-foundation.org and you should be directed to the new version
# - if your release is the latest major version, (i.e. 12 not 11 or 1) then issue a pull request to pull your changes from the version trunk into the master branch, so that the master branch remains the most stable release
# - checkout the version branch again
# - update the pom files to reflect the next minor/patch version's snapshot label (e.g. 12.0.2-SNAPSHOT)
# - if using artifactory or another maven repository, be sure to upload the gsf-parent pom.xml file, gsf-core-x.jar and gsf-legacy-x.jar to the repo.
