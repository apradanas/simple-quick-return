Simple Quick Return
===================

Simple [QuickReturn UI Design Pattern](https://plus.google.com/u/0/+RomanNurik/posts/1Sb549FvpJt) for Android ListView

##Screenshot
![sample](https://github.com/apradanas/simple-quick-return/blob/master/screenshots/simplequickreturn-demo.gif)

##Setup
Import this library to your application as a library project

#Usage
Create XML layout or you can use activity XML layout. Put ListView component with id=@android:id/list

	<ListView
	    android:id="@android:id/list"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent" />

Create your custom header and/or footer XML. Put code below into your activity class

####Header only

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = new SimpleQuickReturn(getApplicationContext())
					.setContent(R.layout.activity_list_view_sample)
					.setHeader(R.layout.header)
					.createView();
		setContentView(view);

		.....
	}

####Footer only

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = new SimpleQuickReturn(getApplicationContext())
					.setContent(R.layout.activity_list_view_sample)
					.setFooter(R.layout.footer)
					.createView();
		setContentView(view);

		.....
	}

####Header and footer

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = new SimpleQuickReturn(getApplicationContext())
					.setContent(R.layout.activity_list_view_sample)
					.setHeader(R.layout.header)
					.setFooter(R.layout.footer)
					.createView();
		setContentView(view);

		.....
	}