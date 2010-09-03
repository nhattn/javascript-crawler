<html>
<head>
</head>
<body>
<%
    for (int i = 0; i < 100; i++) {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        out.write(i + "<br");
    }
%>

</body>
</html>