#!/bin/sh

#touch /etc/init.d/shotbox
sudo dpkg -r shotbox
sudo dpkg --purge --force-all shotbox