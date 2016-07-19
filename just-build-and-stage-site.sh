#!/bin/bash
set -o nounset
set -o errexit
VERSION=`python -c "from xml.dom.minidom import parse;dom = parse('pom.xml');print [n.firstChild for n in dom.getElementsByTagName('version') if n.parentNode == dom.childNodes[0]][0].toxml()"`
echo "Packaging GSF version $VERSION"

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

echo

