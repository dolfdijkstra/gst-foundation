@REM
@REM Copyright 2010 FatWire Corporation. All Rights Reserved.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

IF NOT EXIST cs.jar (
    echo "cs.jar is not found  in your current working directory, exiting."
    echo "The Content Server jar files are NOT registered in your local maven repository."
    exit /B 1
)

set /p VERSION="Please provide the version of Content Server and press [ENTER]: "

cmd /c mvn -B install:install-file -Dfile=assetapi-impl.jar -DgroupId=com.fatwire.cs -DartifactId=assetapi-impl -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=assetapi.jar -DgroupId=com.fatwire.cs -DartifactId=assetapi -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=assetframework.jar -DgroupId=com.fatwire.cs -DartifactId=assetframework -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=assetmaker.jar -DgroupId=com.fatwire.cs -DartifactId=assetmaker -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=basic.jar -DgroupId=com.fatwire.cs -DartifactId=basic -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=batch.jar -DgroupId=com.fatwire.cs -DartifactId=batch -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=catalog.jar -DgroupId=com.fatwire.cs -DartifactId=catalog -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=commercedata.jar -DgroupId=com.fatwire.cs -DartifactId=commercedata -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=cs-core.jar -DgroupId=com.fatwire.cs -DartifactId=cs-core -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=cs-portlet.jar -DgroupId=com.fatwire.cs -DartifactId=cs-portlet -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=cs.jar -DgroupId=com.fatwire.cs -DartifactId=cs -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=cscommerce.jar -DgroupId=com.fatwire.cs -DartifactId=cscommerce -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=directory.jar -DgroupId=com.fatwire.cs -DartifactId=directory -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=firstsite-filter.jar -DgroupId=com.fatwire.cs -DartifactId=firstsite-filter -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=firstsite-uri.jar -DgroupId=com.fatwire.cs -DartifactId=firstsite-uri -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=flame.jar -DgroupId=com.fatwire.cs -DartifactId=flame -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=framework.jar -DgroupId=com.fatwire.cs -DartifactId=framework -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=gator.jar -DgroupId=com.fatwire.cs -DartifactId=gator -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=gatorbulk.jar -DgroupId=com.fatwire.cs -DartifactId=gatorbulk -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=ics.jar -DgroupId=com.fatwire.cs -DartifactId=ics -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=logging.jar -DgroupId=com.fatwire.cs -DartifactId=logging -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=lucene-search.jar -DgroupId=com.fatwire.cs -DartifactId=lucene-search -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=MSXML.jar -DgroupId=com.fatwire.cs -DartifactId=MSXML -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=rules.jar -DgroupId=com.fatwire.cs -DartifactId=rules -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=sampleasset.jar -DgroupId=com.fatwire.cs -DartifactId=sampleasset -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=spark.jar -DgroupId=com.fatwire.cs -DartifactId=spark -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=sparksample.jar -DgroupId=com.fatwire.cs -DartifactId=sparksample -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=sseed.jar -DgroupId=com.fatwire.cs -DartifactId=sseed -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=sserve.jar -DgroupId=com.fatwire.cs -DartifactId=sserve -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=transformer.jar -DgroupId=com.fatwire.cs -DartifactId=transformer -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=visitor.jar -DgroupId=com.fatwire.cs -DartifactId=visitor -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true
cmd /c mvn -B install:install-file -Dfile=xcelerate.jar -DgroupId=com.fatwire.cs -DartifactId=xcelerate -Dversion=%VERSION% -Dpackaging=jar -DgeneratePom=true

