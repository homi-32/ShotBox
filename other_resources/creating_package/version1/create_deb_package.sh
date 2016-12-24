#!/bin/sh

echo "Creating .deb package with ShotBox started"

#TODO: check, if actual OS is debian based, or if it has dpkg 

PRODUCT="shotbox"
VERSION="1.0-1"
AUTHOR="Martin Homola"
CONTACT="homisolutions@gmail.com"
DESCRIPTION="ShotBox\n Universal Java-based software platform for Photo boxes."

CONTROL="Package: "${PRODUCT}"\nVersion: "${VERSION}"\nSection: base\nPriority: optional\nArchitecture: all\nMaintainer: "${AUTHOR}" <"${CONTACT}">\nDescription: "${DESCRIPTION}
POSTINST="#!/bin/sh\nchmod 0755 /opt/shotbox/start.sh\n"
PRERM="#!/bin/sh\nservice shotbox stop\n"
POSTRM="#!/bin/sh\nrm -rfv /opt/shotbox"
LIBS_LOADER_CONFIG_NAME="libs_loader.config"
LIBS_LOADER_CONFIG="#This file serve for storing settings for loading Shotbox External Modules\n#Loading mechanism is part of ShotBox Platform\n#If you did not read manual, you should not touch this config\npath_to_libraries_folder=libs\npackage_with_external_api="
START_SH="#!/bin/sh\nsudo java -jar shotbox.jar"

echo "Creating file structure"

mkdir ${PRODUCT}_${VERSION}
mkdir ${PRODUCT}_${VERSION}/DEBIAN
touch ${PRODUCT}_${VERSION}/DEBIAN/control
echo ${CONTROL} > ${PRODUCT}_${VERSION}/DEBIAN/control
touch ${PRODUCT}_${VERSION}/DEBIAN/postinst
echo ${POSTINST} > ${PRODUCT}_${VERSION}/DEBIAN/postinst
chmod 0755 ${PRODUCT}_${VERSION}/DEBIAN/postinst
touch ${PRODUCT}_${VERSION}/DEBIAN/prerm
echo ${PRERM} > ${PRODUCT}_${VERSION}/DEBIAN/prerm
chmod 0755 ${PRODUCT}_${VERSION}/DEBIAN/prerm
touch ${PRODUCT}_${VERSION}/DEBIAN/postrm
echo ${POSTRM} > ${PRODUCT}_${VERSION}/DEBIAN/postrm
chmod 0755 ${PRODUCT}_${VERSION}/DEBIAN/postrm
mkdir ${PRODUCT}_${VERSION}/opt
mkdir ${PRODUCT}_${VERSION}/opt/shotbox
mkdir ${PRODUCT}_${VERSION}/opt/shotbox/libs
mkdir ${PRODUCT}_${VERSION}/opt/shotbox/logs
mkdir ${PRODUCT}_${VERSION}/opt/shotbox/photos
mkdir ${PRODUCT}_${VERSION}/opt/shotbox/settings
touch ${PRODUCT}_${VERSION}/opt/shotbox/settings/${LIBS_LOADER_CONFIG_NAME}
echo ${LIBS_LOADER_CONFIG} > ${PRODUCT}_${VERSION}/opt/shotbox/settings/${LIBS_LOADER_CONFIG_NAME}
touch ${PRODUCT}_${VERSION}/opt/shotbox/start.sh
echo ${START_SH} > ${PRODUCT}_${VERSION}/opt/shotbox/start.sh

echo "Copying Platfrom to file structure"

cp ../ShotBoxPlatform/target/ShotBoxPlatform*jar-with-dependencies.jar ${PRODUCT}_${VERSION}/opt/shotbox/shotbox.jar

echo "Copying basic libraries to file structure"

cp ../SNEM_ImageHandler_StoringPhotoOnFilesystem/target/SNEM_*jar-with-dependencies.jar ${PRODUCT}_${VERSION}/opt/shotbox/libs/SNEM_ImageHandler_StoringPhotoOnFilesystem.jar
#cp ../SNEM_SimpleCamera_WebCamera/target/SNEM_*jar-with-dependencies.jar ${PRODUCT}_${VERSION}/opt/shotbox/libs/SNEM_SimpleCamera_WebCamera.jar

echo "Generating .deb package "

dpkg-deb --build ${PRODUCT}_${VERSION}
rm ../${PRODUCT}_${VERSION}.deb
mv ${PRODUCT}_${VERSION}.deb ../

echo "Cleaning after generating"

rm -rfv ${PRODUCT}_${VERSION}

echo "Creating .deb package with ShotBox ends"
