package example.com;

import jakarta.servlet.http.HttpServletRequest;

//for absulate url getting from browser
public class Utility {
	public static String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		System.out.println(siteURL);
		return siteURL.replace(request.getServletPath()," ");
		
	}
}
