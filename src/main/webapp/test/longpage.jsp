<html>
<head>

</script>
</head>
<body>
<%
	for(int i=0;i<100;i++){	    
	    out.write(i+"<br>");
	    out.flush();
	    try{
	        Thread.sleep(1000);
	    }catch(Exception e){
	        
	    }
	}
%>
</body>
</html>