#!/bin/sh
echo "Creating .deb package with ShotBox started"
#TODO: check, if actual OS is debian based, or if it has dpkg

VERSION='1.0-1'

echo "Creating file structure"
cp -R shotbox shotbox_${VERSION}

chmod 0755 shotbox_${VERSION}/DEBIAN/postinst
chmod 0755 shotbox_${VERSION}/DEBIAN/prerm
chmod 0755 shotbox_${VERSION}/DEBIAN/postrm

echo "Copying Platfrom to file structure"

cp ../ShotBoxPlatform/target/ShotBoxPlatform*jar-with-dependencies.jar shotbox_${VERSION}/opt/shotbox/shotbox.jar

echo "Copying basic libraries to file structure"

cp ../SNEM_ImageHandler_StoringPhotoOnFilesystem/target/SNEM_*jar-with-dependencies.jar shotbox_${VERSION}/opt/shotbox/libs/SNEM_ImageHandler_StoringPhotoOnFilesystem.jar
#cp ../SNEM_SimpleCamera_WebCamera/target/SNEM_*jar-with-dependencies.jar shotbox_${VERSION}/opt/shotbox/libs/SNEM_SimpleCamera_WebCamera.jar

echo "Generating .deb package "

dpkg-deb --build shotbox_${VERSION}

rm ../shotbox*.deb
mv shotbox_${VERSION}.deb ../
echo "Cleaning after generating"

rm -rfv shotbox_${VERSION}

echo "Creating .deb package with ShotBox ends"
