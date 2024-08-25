/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.contatos;

import jakarta.json.JsonValue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author salvio
 */
public class ApiContatosReceberContatoTest {

    public ApiContatosReceberContatoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of validarParamentros method, of class ApiContatosReceberContato.
     */
    @Test
    public void testValidarParamentros() throws Exception {
        System.out.println("validarParamentros");
        ApiContatosReceberContato instance = new ApiContatosReceberContato();
        instance.salvarContato(JsonValue.EMPTY_JSON_OBJECT);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
