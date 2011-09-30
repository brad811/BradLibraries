<?php

class DbHandler
{
	private $dbhost = 'dbUrl';
	private $dbuser = 'username';
	private $dbpass = 'password';
	private $dbname = 'dbName';
	public $conn;
	
    function __construct()
    {
		
    }
	
	// Make a connection to the database
	function connect()
	{
		$this->conn = mysql_connect($this->dbhost, $this->dbuser, $this->dbpass)
			or die('Error connecting to mysql');
		mysql_select_db($this->dbname);
	}
	
	// Run a selected query on the database
	function query()
	{
		$this->connect();
		$query = $this->formatQuery( func_get_args() );
		
		$update = mysql_db_query($this->dbname, $query);
		if(!($update)) { /* log error */ }
	}
	
	// Count the number of results for a query
	function countRows()
	{
		$this->connect();
		$query = $this->formatQuery( func_get_args() );
		
		$sql = mysql_query($query); // or die(mysql_error()
		$result = mysql_num_rows($sql);
		return $result;
	}
	
	// Return the first result of a query
	function getFirstRow()
	{
		$this->connect();
		$query = $this->formatQuery( func_get_args() );
		
		$result = 'hello';
		$result = mysql_query($query. " LIMIT 1"); // or die(mysql_error()
		//if(!$result)
		//	return 0;
		if($row = mysql_fetch_array($result, MYSQL_ASSOC))
		{
			return $row;
		}
		else
		{
			return 0;
		}
	}
	
	// Return all the results of a query
	function getRows()
	{
		$this->connect();
		$query = $this->formatQuery( func_get_args() );
		
		$rows = Array();
		$count = 0;
		$result = mysql_query($query); // or die(mysql_error()
		while ($row = mysql_fetch_array($result, MYSQL_ASSOC))
		{
			$rows[$count] = $row;
			$count++;
		}
		return $rows;
	}
	
	// Cleans up and formats the query
	private function formatQuery($args) 
	{
		$this->connect();
		$q = $args[0];
		unset($args[0]);
		// array_walk($args, 'DbHandler::escape');
		foreach($args as &$value)
		{
			$value = mysql_real_escape_string($value);
		}
		
		// construct and run query
		$query = vsprintf($q, $args);
		return $query;
	}
}

?>