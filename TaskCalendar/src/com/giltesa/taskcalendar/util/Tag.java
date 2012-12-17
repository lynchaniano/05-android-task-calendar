/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.util
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/util/Tag.java
*/


package com.giltesa.taskcalendar.util;



public class Tag
{

	private int		id;
	private String	name;
	private String	color;
	private int		counter;



	public Tag(int id, String name, String color, int counter)
	{
		this.id = id;
		this.name = name;
		this.color = color;
		this.counter = counter;

	}



	public int getID()
	{
		return id;
	}



	public String getName()
	{
		return name;
	}



	public String getColor()
	{
		return color;
	}



	public int getCounter()
	{
		return counter;
	}



	public void setID(int id)
	{
		this.id = id;
	}



	public void setName(String name)
	{
		this.name = name;
	}



	public void setColor(String color)
	{
		this.color = color;
	}



	public void setCounter(int counter)
	{
		this.counter = counter;
	}

}
