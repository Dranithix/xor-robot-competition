#include "main.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char * stringBuffer;

int buffPos = 0, mm = 0;

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
        case 0: // Motor Control [type, direction, magnitude]
        motor_control(contents[0], contents[1], contents[2]);
				
				if (contents[1] == 1) {
					GPIO_SetBits(GPIOA, GPIO_Pin_12);
				} else {
					GPIO_ResetBits(GPIOA, GPIO_Pin_12);
				}
        break;
        case 1: // Pneumatics Control [type (0 to 3), state]
        pneumatic_control(GPIOB, contents[0] + 5, contents[1]);
        break;
    }
}

void bluetoothInterruptHandler(const uint8_t byte) {
    if (byte == '\n') {
        stringBuffer[buffPos] = '\0';
        handleCommand(stringBuffer);
        buffPos = 0;
        
        memset(stringBuffer, 0, strlen(stringBuffer));
    } else {
        stringBuffer[buffPos++] = byte;
    }
}

void vd_init() {
    RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA,ENABLE);
    
    GPIO_InitTypeDef GPIO_InitStructure;
    GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
    GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IPU;
    GPIO_InitStructure.GPIO_Pin = GPIO_Pin_9 | GPIO_Pin_10;
    
    GPIO_Init(GPIOA, &GPIO_InitStructure);
}

void sendCCD() {
		linear_ccd_read();
		
    uart_tx(COM3, "%s", "CAMERA|");
    for (int i = 0; i < 128; i++) {
      uart_tx(COM3, "%d ", linear_ccd_buffer1[i]);
    }
    
    uart_tx_byte(COM3, '\r');
}

void sendVoltageReceiver() {
    uart_tx(COM3, "GRAB|%d|%d\r", GPIO_ReadInputDataBit(GPIOA, GPIO_Pin_9), GPIO_ReadInputDataBit(GPIOA, GPIO_Pin_10));
}

void sendUltrasonicSensor() {
	uart_tx_byte(COM1, 0x55);
	uart_tx(COM3, "%s|%d\r", "DISTANCE", uart_rx_byte(COM1) * 255 + uart_rx_byte(COM1));
}

void fix_init() {
	GPIO_InitTypeDef GPIO_InitStructure;
		GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
		GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;
		GPIO_InitStructure.GPIO_Pin = GPIO_Pin_12;

		GPIO_Init(GPIOA, &GPIO_InitStructure);
}

int main() {
    stringBuffer = calloc(1, sizeof(char));
    
    ticks_init();
    adc_init();
	
    uart_init(COM3, 115200);

    uart_interrupt_init(COM3, &bluetoothInterruptHandler);
    
    tft_init(0, BLACK, SKY_BLUE, GREEN);
    linear_ccd_init();
    pneumatic_init();
		fix_init();
    vd_init();
    
    motor_init();
	
   
    while (1) {
			sendCCD();
			sendVoltageReceiver();
    }
    
    return 0;
}