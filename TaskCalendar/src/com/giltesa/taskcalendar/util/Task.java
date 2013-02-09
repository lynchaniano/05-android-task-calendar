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


public class Task
{

	private int		id;
	private int		idTag;
	private String	title;
	private String	description;
	private String	date;
	private String	color;



	public Task(int id, int idTag, String date, String title, String description, String color)
	{
		this.id = id;
		this.idTag = idTag;
		this.date = date;
		this.title = title;
		this.description = description;
		this.color = color;
	}



	public int getID()
	{
		return id;
	}



	public int getIDTag()
	{
		return idTag;
	}



	public String getTitle()
	{
		return title;
	}



	public String getDescription()
	{
		return description;
	}



	public String getDate()
	{
		return date;
	}



	public String getColor()
	{
		return color;
	}



	public void setId(int id)
	{
		this.id = id;
	}



	public void setIdTag(int idTag)
	{
		this.idTag = idTag;
	}



	public void setTitle(String title)
	{
		this.title = title;
	}



	public void setDescription(String description)
	{
		this.description = description;
	}



	public void setDate(String date)
	{
		this.date = date;
	}



	public void setColor(String color)
	{
		this.color = color;
	}



	@Override
	public String toString()
	{
		return title;
	}


}
