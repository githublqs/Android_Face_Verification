package com.tec.jca.faceverification.domain;


public interface SFRInit {

	  interface Callback {
	    void onInitReturn(final boolean ret);

	   
	  }

	  void execute(Callback callback);
	}