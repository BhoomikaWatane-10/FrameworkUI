package com.luminor.operations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import com.luminor.base.BaseTest;
import com.tmb.reports.Reporting;
import com.tmb.utils.SslconfigUtils;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetToken extends BaseTest {
	static PerformOperations common = new PerformOperations();

	@BeforeTest
	public static String getAccessToken(String username, String personalCode)
			throws FileNotFoundException, InterruptedException {

		String Bearertoken = "";
		String getCurrentURL = "";

		String clientId = prop.getProperty("clientId");
		String baseURI = prop.getProperty("baseURI");
		
		Thread.sleep(LONG_WAIT);
		if (common.waitForvisible(By.id("smartid"))) {
			driver.findElement(By.id("smartid")).click();
			Reporting.test.pass("clicked on SMART ID");
			Thread.sleep(SMALL_WAIT);
			driver.findElement(By.id("idToken4")).sendKeys(username);// "7939112"
			Reporting.test.pass("enter the User ID");
			Thread.sleep(SMALL_WAIT);
			driver.findElement(By.id("idToken6")).sendKeys(personalCode);// "46401266973"
			Reporting.test.pass("enter the Personal code");
			Thread.sleep(SMALL_WAIT);
			driver.findElement(By.id("idToken15_0")).click();
			Reporting.test.pass("clicked on Log In");
			Thread.sleep(LONG_WAIT);
		} else {
			Reporting.test.fail("URL is not Loaded");
		}
		if (common.waitForvisible(By.id("idToken3_0"))) {
			driver.findElement(By.id("idToken3_0")).click();
			Reporting.test.pass("Confirm the code : clicked on confirm Button");
			Thread.sleep(10000);
			getCurrentURL = driver.getCurrentUrl();
		}
		RestAssured.config = RestAssured.config().sslConfig(SslconfigUtils.getSslConfig());
		String match = getCurrentURL;// "https://localhost/login?code=GE9xCeiLxeodyTnzGxIhMaJNsVQ.4576-zz7XzuZZN5Zt31X9DKcWZU&auth=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdXRoX21ldGhvZCI6InNtYXJ0aWQiLCJuYmYiOjE2MjkzNTMwNTksImF1dGhfdmVyaWZ5X3NtYXJ0aWQiOiJhZTE3MGU0NDU4YzVhOGEwZGNkZGMzNTc3YTc2ZjZmMjIwNGNmNDI3MmRlYzkxM2I3ZWYwYjcyMmYwMmIwY2FiIiwiZXhwIjoxNjI5MzU2MDU5LCJsb2NhbGUiOiJlbiIsImlhdCI6MTYyOTM1MzA1OSwianRpIjoiODI0YzYzZTYtMmEyOC00ODBkLWJlOTktYjgwYjAwYTg2YzRmIn0.U27VSi2cWqSAVxW2gcOVRSBain2RSZ1z81QwyiB50An45Nz4m3awZqhIa5-ql12rMiBrm0IeVhKKR-udk4tE3Kve3beeySIDMxbjGGXUGG4ZJkGC_obfFpfFYFWxwP3hyfzaKecAl1ue6TOiQvyZc-ZqiNL88I9skxVsNw-YLlSQGhlCLLNlb8jOJ7vrsxwoigtOoZazeaa03FolJTXXBU1Y7aBssIR_jEqT4Tpw0RdpiaQJthZgNBAiIXthPq2RZfcs_ddZ0JWyaMmLRVQ76ggQaxmwKhrz6cKwkzt3BXVdX87aR0-NNNxUZytY1aUQDlv8NvUX0Fzkx2CEM2SEqA&iss=https%3A%2F%2Flogin.stg.lumsolutions.net%2Fv1%2Fam%2Foauth2%2Frealms%2Froot%2Frealms%2Fpsd2&state=ttr2TU&client_id=f1465c9e-e220-4385-95c5-f15773333a76";
		Pattern patternForCode = Pattern.compile("code=(.*?)&");
		Matcher matcherForCode = patternForCode.matcher(match);
		while (matcherForCode.find()) {
			System.out.println(matcherForCode.group());
			String apiCode = matcherForCode.group();
			apiCode = apiCode.replaceAll("code=", "");
			apiCode = apiCode.replaceAll("&", "");
			System.out.println(apiCode);

			Response res = RestAssured.given().baseUri((baseURI))// .urlEncodingEnabled(true)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.queryParams("realm", "psd2", "client_id", clientId, "grant_type", "authorization_code",
							"redirect_uri", "https://localhost/login", "code", apiCode)
					.when().post(("/openam/oauth2/access_token"));
			// System.out.println(res.getSessionId());
			System.out.println(res.statusCode());
			// System.out.println(res.prettyPrint());
			String Access_Token = res.asString();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			// System.out.println(Access_Token);

			Pattern patternForRefreshToken = Pattern.compile("\"refresh_token\":\"(.*?),\"auth_method\"");
			Matcher matcherForRefreshtoken = patternForRefreshToken.matcher(Access_Token);
			System.out.println(matcherForRefreshtoken);
			System.out.println(matcherForRefreshtoken.find());
			// while (m.find()==true) {
			// System.out.println(m.group());
			String gen_refresh_token = matcherForRefreshtoken.group();
			gen_refresh_token = gen_refresh_token.replaceAll("\"refresh_token\":\"", "");
			gen_refresh_token = gen_refresh_token.replaceAll(",\"auth_method\"", "");
			System.out.println(gen_refresh_token);

			Pattern patternforIdtoken = Pattern.compile("\"id_token\":\"(.*?),\"token_type\"");
			Matcher matcherforIdtoken = patternforIdtoken.matcher(Access_Token);
			System.out.println(matcherforIdtoken);
			System.out.println(matcherforIdtoken.find());
			// while (m.find()==true) {
			// System.out.println(m.group());
			String gen_Id_token = matcherforIdtoken.group();
			gen_Id_token = gen_Id_token.replaceAll("\"id_token\":\"", "");
			gen_Id_token = gen_Id_token.replaceAll(",\"token_type\"", "");
			System.out.println(gen_Id_token);

			Response res1 = RestAssured.given().baseUri(baseURI)// .
					// urlEncodingEnabled(true)
					.header("Content-Type", "application/x-www-form-urlencoded")
					// .cookie(myCookie)
					.header("Cookie", "tgtcookie=" + gen_Id_token)
					.queryParams("realm", "psd2", "client_id", clientId, "grant_type", "refresh_token", "redirect_uri",
							"https://localhost/login", "code", apiCode, "refresh_token", gen_refresh_token, "client_id",
							clientId)
					.when().post("/openam/oauth2/access_token");

			System.out.println(res1.statusCode());
			String refreshtoken = res1.asString();
			System.out.println(refreshtoken);
			if (res1.statusCode() == 200) {
				Pattern patternforBearertoken = Pattern.compile("\"access_token\":\"(.*?),\"auth_method\"");
				Matcher matcherForBearerToken = patternforBearertoken.matcher(refreshtoken);
				System.out.println(matcherForBearerToken);
				System.out.println(matcherForBearerToken.find());
				// while (m.find()==true) {
				// System.out.println(m.group());
				Bearertoken = matcherForBearerToken.group();
				Bearertoken = Bearertoken.replaceAll("\"access_token\":\"", "");
				Bearertoken = Bearertoken.replaceAll(",\"auth_method\"", "");
				System.out.println(Bearertoken);
				Reporting.test.pass("Bearer token is generated" + Bearertoken);

			} else {
				Reporting.test.fail("Bearer token is not generated" + res1.getBody().asPrettyString());

			}

		}

		return Bearertoken;
	}

}
