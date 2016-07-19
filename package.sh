#!/bin/bash
set -o nounset
set -o errexit
VERSION=`python -c "from xml.dom.minidom import parse;dom = parse('pom.xml');print [n.firstChild for n in dom.getElementsByTagName('version') if n.parentNode == dom.childNodes[0]][0].toxml()"`
echo "GST Site Foundation packager"
echo "Building GSF version $VERSION"

tmpLocation=/tmp/gsf-deploy/gsf-$VERSION
trap onexit ERR


function onexit() {
    local exit_status=${1:-$?}
    echo
    echo
    echo "An error occured. In many case this is caused by missing artifacts from the local maven repository."
    echo "Try running"
    echo '(cd gsf-build-tools && mvn -q -Dmaven.test.skip=true install && cd .. && mvn install && mvn -P '\''!samples'\'' site)'
    echo "mvn -o site:stage -P '!samples'" -DstagingDirectory=""$tmpLocation/site""
    echo "and then try to run package.sh again."
    echo "the output of the failed build is propably in /tmp/mvn-gsf.out."
    exit $exit_status
}

if [ ! -d $HOME/.m2/repository/com/fatwire/gst/gst-foundation ] ;
then 
   echo "The GSF artifacts are not present in your maven repository. This is expected if you are building for the first time on this computer."
   echo "Starting initial build"
   echo
   # first install the build tools
   # then run install on the whole kit to force-download all dependencies (even ones not caught by dependency:go-offline)
   (cd gsf-build-tools && mvn -q -Dmaven.test.skip=true clean install && cd ..)
   mvn -q install >/tmp/mvn-gsf.out
   echo "Finished initial build"
fi

echo "Downloading all artifacts"
mvn -q dependency:go-offline >/tmp/mvn-gsf.out
echo "Building jars"
mvn -o clean install >/tmp/mvn-gsf.out
echo "Building site"
echo "  preparing"
mvn -P '!samples' site >/tmp/mvn-gsf.out
if [ -d "$tmpLocation" ] ; then rm -Rf "$tmpLocation" ;fi
mkdir -p "$tmpLocation"
echo "  staging site under $tmpLocation/site"
mvn site:stage -P '!samples' -DstagingDirectory=$tmpLocation/site > /dev/null
echo "  copying JAR files inside $tmpLocation/site/downloads"
if [ ! -d $tmpLocation/site/downloads ] ;
then
	mkdir $tmpLocation/site/downloads
fi
cp gsf-core/target/gsf-core-$VERSION.jar $tmpLocation/site/downloads/
cp gsf-legacy/target/gsf-legacy-$VERSION.jar $tmpLocation/site/downloads/

echo "Assembling kit"
echo "  adding site"
#mkdir -p "$tmpLocation/site"
#cp -R target/staging/* "$tmpLocation/site"
echo "  adding build artifacts"
cp -R gsf-core/target/gsf* "$tmpLocation"
cp -R gsf-legacy/target/gsf* "$tmpLocation"
#mkdir "$tmpLocation/gsf-sample/"
#cp -R gsf-sample/src "$tmpLocation/gsf-sample/"
#cp -R gsf-sample/resources "$tmpLocation/gsf-sample/"
echo "  adding license"
cp LICENSE "$tmpLocation"
echo "  compressing"
if [ ! -d `pwd`/target ] ; then mkdir `pwd`/target ;fi
archive=`pwd`/target/gsf-$VERSION
(cd `dirname "$tmpLocation"` && tar -czf ${archive}.tgz gsf-* && zip -q -r ${archive}.zip gsf-*)

echo "GSF Packaging complete. Kits are ready for pick-up here:"
echo "  ${archive}.tgz"
echo "  ${archive}.zip"
echo




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

