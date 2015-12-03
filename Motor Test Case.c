#include "main.h"

int main() {
	GPIO_switch_init();
	button_init();
	ticks_init();
	LED_INIT();
	int button13=0;
	int button14=0;
	int button15=0;
	LED_OFF(GPIOA, GPIO_Pin_15);
	LED_OFF(GPIOB, GPIO_Pin_3);
	LED_OFF(GPIOB, GPIO_Pin_4);
	while (1) {
		if (read_button(GPIOC, GPIO_Pin_13)==0) {
			_delay_ms(500);
			button13++;
			motor_init();
			motor_control(1, button13%2, 20*(button15%3)+40);
		}
		if (read_button(GPIOC, GPIO_Pin_14)==0) {
			_delay_ms(500);
			button14++;
			servo_init();
			switch (button14%2) {
				case 0:
					servo_control(1, 1800);
					break;
				case 1:
					servo_control(1, 4200);
					break;
			}
		}
		if (read_button(GPIOC, GPIO_Pin_15)==0) {
			_delay_ms(500);
			button15++;
		}
		if (button13%2!=0) {
			LED_ON(GPIOA, GPIO_Pin_15);
		}
		else {
			LED_OFF(GPIOA, GPIO_Pin_15);
		}
		if (button14%2!=0) {
			LED_ON(GPIOB, GPIO_Pin_3);
		}
		else {
			LED_OFF(GPIOB, GPIO_Pin_3);
		}
		if (button15%2!=0) {
			LED_ON(GPIOB, GPIO_Pin_4);
		}
		else {
			LED_OFF(GPIOB, GPIO_Pin_4);
		}
	}
}
