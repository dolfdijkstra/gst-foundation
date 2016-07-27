#!/bin/bash
set -o nounset
set -o errexit
VERSION=`python -c "from xml.dom.minidom import parse;dom = parse('pom.xml');print [n.firstChild for n in dom.getElementsByTagName('version') if n.parentNode == dom.childNodes[0]][0].toxml()"`
echo "GST Site Foundation packager"

execLocation="$PWD"

tmpLocation=/tmp/gsf-deploy/gsf-$VERSION

siteLocation=$tmpLocation/site

kitLocation=$tmpLocation/kit

trap onexit ERR


function onexit() {
    local exit_status=${1:-$?}
    echo
    echo
    echo "An error occurred. Make sure you've built the GSF (e.g. 'sh package.sh') before executing this script as this script skips those steps."
    exit $exit_status
}

echo "Clearing $tmpLocation"
if [ -d "$tmpLocation" ] ; then rm -Rf "$tmpLocation" ;fi
mkdir -p "$tmpLocation"

echo "Building site"

echo "  preparing"
mvn -P '!samples' site >/tmp/gsf-deploy/mvn-gsf.out

echo "  staging site under $siteLocation"
mvn site:stage -P '!samples' -DstagingDirectory=$siteLocation > /dev/null

echo "  initializing 'downloads' folder $siteLocation/downloads"
if [ ! -d $siteLocation/downloads ] ;
then
	mkdir $siteLocation/downloads
fi

echo "  copying JAR files inside $siteLocation/downloads"
cp gsf-core/target/gsf-core-$VERSION.jar $siteLocation/downloads/
cp gsf-legacy/target/gsf-legacy-$VERSION.jar $siteLocation/downloads/

echo "  copying JavaDoc and Source Files inside $siteLocation/downloads"
cp gsf-core/target/gsf-core-$VERSION-javadoc.jar $siteLocation/downloads/
cp gsf-core/target/gsf-core-$VERSION-sources.jar $siteLocation/downloads/
cp gsf-legacy/target/gsf-legacy-$VERSION-javadoc.jar $siteLocation/downloads/
cp gsf-legacy/target/gsf-legacy-$VERSION-sources.jar $siteLocation/downloads/

echo "Adding license to $siteLocation"
cp LICENSE "$siteLocation"

echo "  compressing site"
if [ ! -d `pwd`/target ] ; then mkdir `pwd`/target ;fi
websiteArchive=`pwd`/target/gsf-$VERSION-website
cd $tmpLocation
tar -czf ${websiteArchive}.tgz site
zip -q -r ${websiteArchive}.zip site

echo "GSF's website is ready for pick-up here:"
echo "  ${websiteArchive}.tgz"
echo "  ${websiteArchive}.zip"
echo

cd $execLocation