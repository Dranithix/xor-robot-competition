#include "stm32f10x_gpio.h"

#define BUTTON1 GPIO_Pin_13
#define BUTTON2 GPIO_Pin_14
#define BUTTON3 GPIO_Pin_15
#define BUTTON_PORT GPIOC



void button_init();
u8 read_button(GPIO_TypeDef* PORT,u16 gpio_pin);
void check_button();