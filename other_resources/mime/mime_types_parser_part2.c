#include<stdio.h>
#include<stdlib.h>

int main(void)
{
    FILE * fp, * fp2;
    char * line = NULL;
    size_t len = 0;
    ssize_t read;

    fp = fopen("output_mime.txt", "r");
    if (fp == NULL)
        exit(EXIT_FAILURE);

    fp2 = fopen("output_mime2.txt", "w+");
    if (fp2 == NULL)
        exit(EXIT_FAILURE);


    while ((read = getline(&line, &len, fp)) != -1) {
//        printf("Retrieved line of length %zu :\n", read);
//        printf("%s", line);

    int i = 0;
    while(1){
    if(line[i] == '\"'){i++; while(line[i]!='\"'){fputc(line[i], fp2);i++;}fputs(line,fp2);break;}
    i++;
    }
    }

    fclose(fp);
    fclose(fp2);
    if (line)
        free(line);
    exit(EXIT_SUCCESS);
}
