https://www.google.sk/search?q=create+.deb+package&oq=create+.deb+package&aqs=chrome..69i57.6827j0j1&client=ubuntu&sourceid=chrome&ie=UTF-8

https://wiki.debian.org/HowToPackageForDebian

https://wiki.debian.org/HowToPackageForDebian#Building_Debian_packages

http://askubuntu.com/questions/1345/what-is-the-simplest-debian-packaging-guide

http://chat.stackexchange.com/rooms/663/conversation/the-basics-of-packaging-on-ubuntu-packaging-part-1

http://packaging.ubuntu.com/html/packaging-new-software.html

-------------------------
use this:
https://ubuntuforums.org/showthread.php?t=910717
https://www.leaseweb.com/labs/2013/06/creating-custom-debian-packages/

shotbox_1.0-1


------------------------
services:
http://unix.stackexchange.com/questions/106656/how-do-services-in-debian-work-and-how-can-i-manage-them
http://unix.stackexchange.com/questions/49626/purpose-and-typical-usage-of-etc-rc-local

how to create and use service:
http://stackoverflow.com/questions/11203483/run-a-java-application-as-a-service-on-linux
http://www.jcgonzalez.com/linux-java-service-wrapper-example
https://debian-administration.org/article/28/Making_scripts_run_at_boot_time_with_Debian

official documentation:
http://www.debian.org/doc/debian-policy/ch-opersys.html#s-sysvinit

kokotina tie servisyyyyyy!!!!!!!!!!!!!!!!!!!!!1
vysvetlenie kus, ako to spustat a co je na co....
http://askubuntu.com/questions/335242/how-to-install-an-init-d-script
naco je /etc/default/ :http://askubuntu.com/questions/429592/what-is-the-purpose-of-etc-default


ako tu kokotinu nainstalovat:
https://debian-administration.org/article/28/Making_scripts_run_at_boot_time_with_Debian

dalsi navod, nakoniec som skoncil pri nom (chybali nejake pojebane .service fajly):
https://github.com/Sonarr/Sonarr/wiki/Autostart-on-Linux#systemd


taaaaaaaakze, ako to zrobis:
 - napisat si script, ktory obsluhuje service
 - zmenit prava scriptu na 755 aby bol vykonatelny
 - nakopirovat script do /etc/init.d/
 - spustit prikaz pre setupnutie servisy pri boote:
 - pri mazani balicka sputit tento prikaz "" a zmazat script z /etc/init.d/shotbox
 - zmazat aj /tmp/shotbox-pid






https://github.com/Sonarr/Sonarr/wiki/Autostart-on-Linux#systemd
