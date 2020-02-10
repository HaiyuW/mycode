Haiyu Wang, 475533


Problem:

For the greeting interface, I try to hide the title and status bar using requestWindowFeature(Window.FEATURE_NO_TITLE), but this didn't help.

Creative Portion:

1. Greeting Interface
Description:
On opening the application, the Greeting Interface will show up for 3 seconds. 

Why:
It displays the name of the application and also give the users intuitive feeling about the application.

How:
1) Create new .xml containing the appearance of the greeting interface
2) Create a new class for the GreetingActivity.
3) Use handler for the delay to call the MainActivity
4) Modify the AndroidManifest.xml to evoke the GreetingActivity ahead of the MainActivity.


2. Warn when over the set calories
Description:
When the Remain Calories become negative, raise a warning

Why:
Give users a remind that they have surpass the set calories which can be helpful 

How:
1) After adding the food, check if the Remain Calories are negative.
2) If it is negative, raise a Toast saying "Over Set Calories

3. Clear Input after submitting
Description:
After submitting the food and calories, clear the input box

Why:
It is very convenient for user to input another food and calorie

How:
At the end of the function addFood(), use newFood.setText(""), newCal.setText("") to clear the text.


