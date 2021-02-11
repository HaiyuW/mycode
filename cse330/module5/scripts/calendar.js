/* * * * * * * * * * * * * * * * * * * *\
 *               Module 4              *
 *      Calendar Helper Functions      *
 *                                     *
 *        by Shane Carr '15 (TA)       *
 *  Washington University in St. Louis *
 *    Department of Computer Science   *
 *               CSE 330S              *
 *                                     *
 *      Last Update: October 2017      *
\* * * * * * * * * * * * * * * * * * * */

/*  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

(function () {
	"use strict";

	/* Date.prototype.deltaDays(n)
	 * 
	 * Returns a Date object n days in the future.
	 */
	Date.prototype.deltaDays = function (n) {
		// relies on the Date object to automatically wrap between months for us
		return new Date(this.getFullYear(), this.getMonth(), this.getDate() + n);
	};

	/* Date.prototype.getSunday()
	 * 
	 * Returns the Sunday nearest in the past to this date (inclusive)
	 */
	Date.prototype.getSunday = function () {
		return this.deltaDays(-1 * this.getDay());
	};
}());

/** Week
 *
 * Represents a week.
 *
 * Functions (Methods):
 *	.nextWeek() returns a Week object sequentially in the future
 *	.prevWeek() returns a Week object sequentially in the past
 *	.contains(date) returns true if this week's sunday is the same
 *		as date's sunday; false otherwise
 *	.getDates() returns an Array containing 7 Date objects, each representing
 *		one of the seven days in this month
 */
function Week(initial_d) {
	"use strict";

	this.sunday = initial_d.getSunday();


	this.nextWeek = function () {
		return new Week(this.sunday.deltaDays(7));
	};

	this.prevWeek = function () {
		return new Week(this.sunday.deltaDays(-7));
	};

	this.contains = function (d) {
		return (this.sunday.valueOf() === d.getSunday().valueOf());
	};

	this.getDates = function () {
		let dates = [];
		for(let i=0; i<7; i++){
			dates.push(this.sunday.deltaDays(i));
		}
		return dates;
	};
}

/** Month
 *
 * Represents a month.
 *
 * Properties:
 *	.year == the year associated with the month
 *	.month == the month number (January = 0)
 *
 * Functions (Methods):
 *	.nextMonth() returns a Month object sequentially in the future
 *	.prevMonth() returns a Month object sequentially in the past
 *	.getDateObject(d) returns a Date object representing the date
 *		d in the month
 *	.getWeeks() returns an Array containing all weeks spanned by the
 *		month; the weeks are represented as Week objects
 */
function Month(year, month) {
	"use strict";

	this.year = year;
	this.month = month;

	this.nextMonth = function () {
		return new Month( year + Math.floor((month+1)/12), (month+1) % 12);
	};

	this.prevMonth = function () {
		return new Month( year + Math.floor((month-1)/12), (month+11) % 12);
	};

	this.getDateObject = function(d) {
		return new Date(this.year, this.month, d);
	};

	this.getWeeks = function () {
		let firstDay = this.getDateObject(1);
		let lastDay = this.nextMonth().getDateObject(0);

		let weeks = [];
		let currweek = new Week(firstDay);
		weeks.push(currweek);
		while(!currweek.contains(lastDay)){
			currweek = currweek.nextWeek();
			weeks.push(currweek);
		}

		return weeks;
	};
}

// Get the name of the Month
function transMonth(month) {
    switch (month) {
        case 0: return "January";
        case 1: return "February";
        case 2: return "March";
        case 3: return "April";
        case 4: return "May";
        case 5: return "June";
        case 6: return "July";
        case 7: return "August";
        case 8: return "September";
        case 9: return "October";
        case 10: return "November";
        case 11: return "December";
    }
}

// function updateCalendarWithCheckLogin(currentMonth) {
// 	let result = checkLogin(loginStatus) ;
// 	function loginStatus(data) {
// 		updateCalendar(currentMonth, data.loggedin)
// 	}
// }


// update the calendar view.
function updateCalendar(currentMonth) {

    // the first line show the month and year.
    document.getElementById("year_month").textContent = transMonth(currentMonth.month) + " " + currentMonth.year;

	document.getElementById("calendar").setAttribute("display", "grid");

    // refresh the calendar div
    document.getElementById("calendar").innerHTML =
		"<div class=\"colofdate\"><p>Sun</p></div>\n" +
		"<div class=\"colofdate\"><p>Mon</p></div>\n" +
		"<div class=\"colofdate\"><p>Tue</p></div>\n" +
		"<div class=\"colofdate\"><p>Wed</p></div>\n" +
		"<div class=\"colofdate\"><p>Thu</p></div>\n" +
		"<div class=\"colofdate\"><p>Fri</p></div>\n" +
		"<div class=\"colofdate\"><p>Sat</p></div>\n";



    let cols = document.getElementsByClassName("colofdate");

    // get the first day and last day of a month.
    let firstDay = currentMonth.getDateObject(1);
    let lastDay = currentMonth.nextMonth().getDateObject(0);

    let dayNum = lastDay.getDate();

    // if the first day of the month is not sunday, then add some spaces
    let spaceNum = Number(firstDay.getDay());

    if (spaceNum > 0) {
        for (let i = 0; i < spaceNum; i++){
            let space = document.createElement("div");
            space.appendChild(document.createTextNode('\u00A0'));
            cols[i].appendChild(space);
        }
    }

    // fill in the calendar
    let day = firstDay;
    let i = 1;

    while (i <= dayNum){
        let daytime = document.createElement("div");
        const date = currentMonth.year.toString() + "-" + (currentMonth.month + 1).toString() +  "-"  + day.getDate().toString();
        if (day.getDate() == currentDate.getDate() && day.getMonth() == currentDate.getMonth() && day.getFullYear() == currentDate.getFullYear()){
        	daytime.setAttribute("style", "font-weight: bold");
		}
        daytime.setAttribute("id", date);
        daytime.setAttribute("style", "padding: 5px");
        daytime.appendChild(document.createTextNode(day.getDate().toString()));
        cols[day.getDay()].appendChild(daytime);
        i++;
        day = currentMonth.getDateObject(i);
        daytime.addEventListener("click", function () {
			updateEvents(date, handleUpdateEvents);
		}, false);
    }
}



// after loading, show the current date
let currentDate = new Date();
let currentMonth = new Month(currentDate.getFullYear(), currentDate.getMonth());

updateCalendar(currentMonth);

document.getElementById("next_month_btn").addEventListener("click",
	function () {
		let nextMonth = currentMonth.nextMonth();
		currentMonth = nextMonth;
		updateCalendar(currentMonth);
	}, false);

document.getElementById("prev_month_btn").addEventListener("click",
    function () {
        let prevMonth = currentMonth.prevMonth();
        currentMonth = prevMonth;
        updateCalendar(currentMonth);
    }, false);




/* --- some functions relate to events --- */
function getEventsByDate(date) {
	alert(date);
}



