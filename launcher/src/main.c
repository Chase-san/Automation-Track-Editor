#define _WIN32_WINNT 0x0502
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <wchar.h>
#include <windows.h>

#define BUFFER_SIZE	4096

char *getJavaHome() {
	HKEY hk;
	
	if(RegOpenKeyEx(HKEY_LOCAL_MACHINE, TEXT("SOFTWARE\\JavaSoft\\Java Runtime Environment"), 0, KEY_READ | KEY_WOW64_64KEY, &hk) == ERROR_SUCCESS) {
		
		char *buf = malloc(BUFFER_SIZE);
		if(buf == NULL) {
			return NULL;
		}
		char *data = malloc(BUFFER_SIZE);
		if(data == NULL) {
			free(buf);
			return NULL;
		}
		unsigned long bsize = BUFFER_SIZE;
		unsigned long dsize = BUFFER_SIZE;
		
		int index = 0;
		while(RegEnumValue(hk, index, buf, &bsize, NULL, NULL, (LPBYTE)data, &dsize) == ERROR_SUCCESS) {
			if(!strcmp(buf, "CurrentVersion")) {
				index = -1;
				break;
			}
			bsize = dsize = BUFFER_SIZE;
			++index;
		}
		RegCloseKey(hk);
		if(index != -1) {
			free(buf);
			free(data);
			return NULL;
		}
		sprintf(buf, "SOFTWARE\\JavaSoft\\Java Runtime Environment\\%s", data);
		
		if(RegOpenKeyEx(HKEY_LOCAL_MACHINE, TEXT(buf), 0, KEY_READ | KEY_WOW64_64KEY, &hk) == ERROR_SUCCESS) {
			int error = 0;
			bsize = dsize = BUFFER_SIZE;
			index = 0;
			while((error = RegEnumValue(hk, index, buf, &bsize, NULL, NULL, (LPBYTE)data, &dsize)) == ERROR_SUCCESS) {
				//printf("%s = %s\n", buf, data);
				if(!strcmp(buf, "JavaHome")) {
					index = -1;
					break;
				}
				bsize = dsize = BUFFER_SIZE;
				++index;
			}
			RegCloseKey(hk);
			if(index != -1) {
				free(buf);
				free(data);
				return NULL;
			}
			//we now have the path!
			char *output = malloc(dsize+1);
			strcpy(output, data);
			
			free(buf);
			free(data);
			return output;
		}
	}
	return NULL;
}

/*
 * Poor Mans Windows Java Unicode Command Line Passer.
 * Version 2
 */
int main() {
	char *jargs = malloc(BUFFER_SIZE);
	if(jargs == NULL) {
		return 1;
	}
	
	jargs[0] = 0;
	strcat(jargs,"-jar \"");
	{
		char *path = malloc(BUFFER_SIZE);
		if(path == NULL) {
			return 1;
		}
		memset(path,0,BUFFER_SIZE);
		GetModuleFileName(NULL,path,BUFFER_SIZE);
		//find last occurance of \, replace it with 0 (terminator)
		for(int i=BUFFER_SIZE-1;i>=0;--i) {
			if((int)path[i]==92) {
				path[i] = 0;
				break;
			}
		}
		
		strcat(jargs, path);
		free(path);
	}
	
	HINSTANCE handle = GetModuleHandle(NULL);
	if(handle == NULL) {
		return 2;
	}
	char *jarname = malloc(BUFFER_SIZE);
	if(jarname == NULL) {
		return 1;
	}
	LoadString(handle, 0, jarname, BUFFER_SIZE);
	
	strcat(jargs, "\\");
	strcat(jargs, jarname);
	strcat(jargs, "\"");
	free(jarname);
	
	int value = (int)ShellExecute(NULL,"open","javaw.exe",jargs,NULL,SW_SHOWNORMAL);
	if(value < 32) {
		char *buf = malloc(BUFFER_SIZE);
		if(buf == NULL) {
			return 1;
		}
		char *jhome = getJavaHome();
		if(jhome == NULL) {
			free(buf);
			return 4;
		}
		sprintf(buf,"%s\\bin\\javaw.exe", jhome);
		free(jhome);
		
		value = (int)ShellExecute(NULL,"open",buf,jargs,NULL,SW_SHOWNORMAL);
		free(buf);
		if(value < 32) {
			return 3;
		}
	}
	
	return 0;
}
