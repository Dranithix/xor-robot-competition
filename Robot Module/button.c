#include "button.h"
#include "buzzer_song.h"


void button_init(){

	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOC,ENABLE);
	
	GPIO_InitTypeDef GPIO_InitStructure;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IPU;
	GPIO_InitStructure.GPIO_Pin = BUTTON1 | BUTTON2 | BUTTON3;
	
	GPIO_Init(GPIOC, &GPIO_InitStructure);

}
	
u8 read_button(GPIO_TypeDef* PORT,u16 gpio_pin)
{

return	GPIO_ReadInputDataBit(PORT,gpio_pin);

}