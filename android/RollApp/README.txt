Haiyu Wang
Student ID: 475533

Creative Portion

1. Show Roll History
Description:
(1) There is a button at RollActivity page named 'HISTORY'. 
(2) After clicking it, it will head to  HistoryActivity and show the list of the results of previous rolls. 
(3) It also has a summary under the list. 
(4) Finally, it has a button back to RollActivity

Reason:
Users may not only pay attention to the highest, lowest and average results of the rolls but also be curious about each result of the rolls. Thus, I list the results of the rolls and also provide the summary of the rolls

Implementation:
(1) Create a HistoryActivity and use Intent to switch to that activity. 
(2) Use RollAdapter to display the resultList and notice the changes of the rollList. 
(3) Traverse the rollList to get the summary of the previous results.
(4) Use Intent to head back to RollActivity


2. Undo Function
Description:
(1) There is a button at RollActivity page named 'UNDO'
(2) After clicking it, it will abandon the current roll result
(3) The previous result will show again and the history will behave properly.

Reason:
Since it has a summary at the history page, users may not be satisfied about the current result and don't want to record the result.
It is different from 'REROLL', since 'REROLL' simply takes another roll but record the result. 'UNDO' abandons the current roll and displays the previous result.

Implementation:
(1) After clicking the 'UNDO' button, rollList and seqList remove the last member.
(2) It takes out the current last member, chooses the right color and displays on the screen.


3. Warning When Typing Error
Description:
If users don't enter both 'Dice Number' and 'Max Dice Value', issue a waring.

Reason:
Guarantee the right inputs

Implementation:
Use toast to give warnings.



Files:
1. Activities:
MainActivity, RollActivity, StatsActivity, HistoryActivity, RollAdapter

2. Fragments:
HighFragment, LowFragment, AvgFragment

3. Layout Files:
activity_main, activity_roll, activity_stats, activity_history, 
fragment_stats, clear_msg, roll_list

4. Files Changed
AndroidManifest.xml, Style.xml




