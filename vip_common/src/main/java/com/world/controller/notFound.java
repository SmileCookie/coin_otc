package com.world.controller;

import com.world.web.Page;
import com.world.web.Pages;

public class notFound extends Pages{
  @Page(Viewer = "/common/404.jsp")
  public void index(){}
}