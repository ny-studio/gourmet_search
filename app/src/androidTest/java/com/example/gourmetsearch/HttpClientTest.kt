package com.example.gourmetsearch

import junit.framework.TestCase

class HttpClientTest : TestCase() {

    val ht = HttpClient(this)

    fun testGetRequestURL() {
    }

    fun testGetURL(){
        assertEquals("https://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key=02eb467934af0e61&keyword=大阪"
            ,ht.createURL("大阪"))
    }
}