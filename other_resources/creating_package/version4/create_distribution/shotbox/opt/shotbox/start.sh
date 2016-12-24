#!/bin/sh
cd /opt/shotbox/
#output is redirected just temporary
sudo java -jar /opt/shotbox/shotbox.jar > /opt/shotbox-out.txt 2> /opt/shotbox-out.txt
