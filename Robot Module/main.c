#include "main.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char * stringBuffer;
int buffPos = 0, count = 0;

void handleCommand(char * command) {
	int dataIndex = 0, contentIndex = 0, header = -1;
	int contents[16];
	for (char * data = strtok(command, "|"); data != NULL; data = strtok(NULL, "|")) {
		if (dataIndex == 0) {
			header = atoi(data);
		} else {
			contents[contentIndex++] = atoi(data);
		}
		dataIndex++;
	}
	
	switch (header) {
		case 0: // Motor Control
			count++;
			break;
	}
}

void bluetoothHandler(const uint8_t byte) {
	if (byte == '\n') {
			handleCommand(stringBuffer);
			buffPos = 0;
		
			memset(stringBuffer, 0, strlen(stringBuffer));
	} else {
			stringBuffer[buffPos++] = byte;
	}
}

int main() {
	stringBuffer = calloc(1, sizeof(char));
	
	uart_init(COM3, 115200);
	uart_interrupt_init(COM3, &bluetoothHandler);
	
	tft_init(0, BLACK, SKY_BLUE, GREEN);
	button_init();
	ticks_init();
	linear_ccd_init();
	motor_init();
	adc_init();

	long lastTick = get_ms_ticks();

	while (1) {
		motor_control(1, 1, 300);

		//if (get_ms_ticks() - lastTick >= 50) {
			linear_ccd_read();
			
			uart_tx(COM3, "%s", "CCD|");
			
			for (int i = 0; i < 128; i++) {
				uart_tx(COM3, "%d ", linear_ccd_buffer1[i]);
			}
			
			uart_tx_byte(COM3, '\n');
			
			tft_prints(0, 0, "%d", count);
			tft_update();
			lastTick = get_ms_ticks();
		//}
	}

	return 0;
}