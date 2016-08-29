#include<stdio.h>
#include<stdlib.h>
#include <unistd.h>
#include <fcntl.h>

//parsing this table: https://www.sitepoint.com/web-foundations/mime-types-complete-list/

int main(){

    char c;

printf("\nstarts");

    int filedesc1 = open("mime.txt", O_RDWR | O_APPEND);
    if(filedesc1 < 0)
        return 1;

printf("\nfile1 opened");
    int filedescX = open("output_mime.txt", O_RDWR | O_APPEND | O_TRUNC);
    if(filedescX < 0)
        return 1;

printf("\nfile2 opened");

   while(c != EOF){
      read(filedesc1, &c, 1);
//      printf("\nreaded: '%c'", c);
      if(c == '.'){
//        printf("\npassing through");
        write(filedescX,"(\"", 2);
        while(c!=EOF && c!='\n'){
//          printf("\ncycle: '%c'", c);
          read(filedesc1, &c, 1);
          if(c=='\t'){ write(filedescX, "\", \"", 4);}
          else if(c=='/'){ write(filedescX, "\", \"", 4);}
          else if(c=='\n'||c==EOF){write(filedescX,"\"),",3); write(filedescX, &c, 1);}
          else write(filedescX,&c, 1);
        }
      }
   }

close(filedesc1);
close(filedescX);

printf("\nend.");

return 0;
}
