/*
 * main.cpp
 *
 *  Created on: 20 Nov 2015
 *  Author: Felicia
 */

#include <iostream>
#include <stdlib.h>     /* srand, rand */
#include <time.h>       /* time */
using namespace std;

int main()
{
	int lastCCD[128]={0};
	int currentCCD[128]={0};

	// Initialise random seed
	srand (time(NULL));

	// Generate random values for CCD
	for (int i=0; i<128; i++)
	{
		int random_num = rand()%160;
		lastCCD[i]=random_num;

		random_num = rand()%160;
		currentCCD[i]=random_num;
	}

	cout << "lastCCD: ";
	for (int i=0; i<128; i++)
	{
		cout << lastCCD[i] << " ";
	}
	cout << endl;

	cout << "currentCCD: ";
	for (int i=0; i<128; i++)
	{
		cout << currentCCD[i] << " ";
	}
	cout << endl;

	int rate_of_change[128] = {0};

	for (int i=0; i<128; i++)
	{
		rate_of_change[i]=currentCCD[i]-lastCCD[i];
	}

	cout << "Rate of change: ";
	for (int i=0; i<128; i++)
	{
		cout << rate_of_change[i] << " ";
	}
	cout << endl;

	// Divide into 3 sections
	int leftCCD=42, middleCCD=44, rightCCD=42;
	int left_roc=0, middle_roc=0, right_roc=0;

	// Add up the rate of change on the left section
	for (int i=0; i<leftCCD; i++)
	{
		left_roc+=rate_of_change[i];
	}
	left_roc=left_roc/leftCCD;
	cout << "Left section: " << left_roc << endl;

	// Add up the rate of change on the middle section
	for (int i=leftCCD; i<leftCCD+middleCCD; i++)
	{
		middle_roc+=rate_of_change[i];
	}
	middle_roc/=middleCCD;
	cout << "Middle section: " << middle_roc << endl;

	// Add up the rate of change on the right section
	for (int i=leftCCD+middleCCD; i<leftCCD+middleCCD+rightCCD; i++)
	{
		right_roc+=rate_of_change[i];
	}
	right_roc/=rightCCD;
	cout << "Right section: " << right_roc << endl;

	/*
	 * STATES
	 * 0 = forward, 1 = turn left, 2 = turn right,
	 * 3 = rotate left, 4 = rotate right, 5 = backwards
	 * 6 = stop
	 */
	int state=0;

	if (left_roc<0 && middle_roc<0 && right_roc<0) //all sections negative
	{
		state=5; //backwards
	}
	if (left_roc<0 && middle_roc<0 && right_roc>0) //right section positive
	{
		state=4; //rotate right
	}
	if (left_roc<0 && middle_roc>0 && right_roc<0) //middle section positive
	{
		state=0; //forward
	}
	if (left_roc<0 && middle_roc>0 && right_roc>0) //middle and right section positive
	{
		state=2; //turn right
	}
	if (left_roc>0 && middle_roc<0 && right_roc<0) //left section positive
	{
		state=3; //rotate left
	}
	if (left_roc>0 && middle_roc<0 && right_roc>0) //left and right section positive
	{
		state=0; //straight
	}
	if (left_roc>0 && middle_roc>0 && right_roc<0) //left and middle section positive
	{
		state=1; //turn left
	}
	if (left_roc<0 && middle_roc<0 && right_roc<0) //all sections positive
	{
		state=6; //stop
	}

	return 0;
}
