#include <string.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdio.h>

void handleCommand(char * command) {

	int dataIndex = 0, contentIndex = 0, header = -1;

	int contents[16];
	char * token = strtok(command, "|");
	while (token != NULL) {
		printf("%d\n", atoi(token));
		token = strtok(NULL, "|");
	}

//	for (int i = 0; i < 14; i++) {
//		printf("%d\n", contents[i]);
//	}
}

int main() {
	printf("%02x", (uint16_t) 0x55);
}
