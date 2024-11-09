package org.pastelin.junit5app.ejemplos.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.pastelin.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;

    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initMethodTest(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Juan", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;

        System.out.println("Iniciando método");

        testReporter.publishEntry("Ejecutando " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName() + " con las etiquetas " + testInfo.getTags());
    }


    @AfterEach
    void tearDown() {
        System.out.println("Finalizando método de prueba");
    }


    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Tag("cuenta")
    @Nested
    @DisplayName("Probando atributos de la cuenta corriente")
    class CuentaTestNombreSaldo {
        @Test
        @DisplayName("Probando nombre")
        void testNombreCuenta() {

            testReporter.publishEntry(testInfo.getTags().toString());
            if (testInfo.getTags().contains("cuenta")) {
                testReporter.publishEntry("Es una cuenta");

            }

            String esperado = "Juan";
            String real = cuenta.getPersona();

            assertNotNull(real, "La cuenta no puede ser nula");
            assertEquals(esperado, real, () -> "El nombre de la cuenta no es el que se esperaba: se esperaba: " + esperado + " y fue: " + real);
            assertEquals("Juan", real, "El nombre de la cuenta no es el que se esperaba");
        }

        @Test
        @DisplayName("Probando el saldo de la cuenta corriente, que no sea nulo, mayor que cero y que sea igual al esperado")
        void testSaldoCuenta() {

            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @Disabled
        @DisplayName("Probando referenciaque sean iguales con el metodo equals")
        void testReferenciaCuenta() {
            cuenta = new Cuenta("John Doe", new BigDecimal("8900.999"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.999"));

            //assertNotEquals(cuenta2, cuenta);
            assertEquals(cuenta2, cuenta);
        }


    }

    @Nested
    class CuentaOperacionesTest {

        @Tag("cuenta")
        @Test
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Test
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Tag("error")
        @Test
        void testDineroInsuficienteExceptionCuenta() {

            Exception exception = assertThrows(DineroInsuficienteException.class, () -> cuenta.debito(new BigDecimal(1500)));

            String actual = exception.getMessage();
            String esperado = "Dinero Insuficiente";
            assertEquals(esperado, actual);
        }

        @Tag("banco")
        @Tag("cuenta")
        @Test
        void testTransferirDineroCuentas() {
            Cuenta cuenta1 = new Cuenta("Juan", new BigDecimal("1000"));
            Cuenta cuenta2 = new Cuenta("John", new BigDecimal("2000.8090"));

            Banco banco = new Banco();
            banco.setNombre("Banco del Estado");
            banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
            assertEquals("1500", cuenta1.getSaldo().toPlainString());
            assertEquals("1500.8090", cuenta2.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Tag("banco")
        @Test
        void testRelacionBancoCuentas() {
            Cuenta cuenta1 = new Cuenta("Juan", new BigDecimal("1000"));
            Cuenta cuenta2 = new Cuenta("John", new BigDecimal("2000.8090"));

            Banco banco = new Banco();
            banco.addCuenta(cuenta1);
            banco.addCuenta(cuenta2);

            banco.setNombre("Banco del Estado");
            banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

            assertAll(
                    () -> assertEquals("1500", cuenta1.getSaldo().toPlainString()),
                    () -> assertEquals("1500.8090", cuenta2.getSaldo().toPlainString()),
                    () -> assertEquals(2, banco.getCuentas().size()),
                    () -> assertEquals("Banco del Estado", cuenta1.getBanco().getNombre()),
                    () -> assertEquals("Juan", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Juan"))
                            .findFirst()
                            .get()
                            .getPersona()),
                    () -> assertTrue(banco.getCuentas().stream()
                            .anyMatch(c -> c.getPersona().equals("Juan"))));


        }
    }

    @Nested
    class SistemaOpertivoTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWIndows() {

        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMac() {

        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {

        }
    }

    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJdk8() {

        }

        @Test
        @EnabledOnJre(JRE.JAVA_17)
        void soloJdk17() {

        }

        @Test
        @DisabledOnJre(JRE.JAVA_17)
        void noJdk17() {

        }
    }

    @Nested
    class SystemPropertiesTest {
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = ".*17.*")
        void testJavaVersion() {

        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {

        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testNO64() {

        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "MI15447")
        void testUserName() {

        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {

        }
    }


    @Nested
    class VariablesAmbienteTest {
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk1.8.*")
        void testJavaHome() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "12")
        void testProcesadores() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
        void testEnv() {

        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testEnvProdDisabled() {

        }

    }


    @Test
    @DisplayName("test Saldo Cuenta Dev")
    void testSaldoCuentaDev() {

        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev);

        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("test Saldo Cuenta Dev 2")
    void testSaldoCuentaDev2() {

        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @DisplayName("Probando debito cuenta Repetir")
    @RepeatedTest(value = 5, name = "{displayName} - Repetición número {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepetir(RepetitionInfo info) {

        if (info.getCurrentRepetition() == 3) {
            System.out.println("Estamos en la repetición " + info.getCurrentRepetition());
        }

        cuenta.debito(new BigDecimal(100));

        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
    @ValueSource(strings = {"100", "200", "300", "500", "700", "1000"})
    void testDebitoCuenta(String monto) {
        cuenta.debito(new BigDecimal(monto));

        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


    @Tag("param")
    @Nested
    class PruebasParametrizadasTest {
        @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1, 100", "2, 200", "3, 300", "4, 500", "5, 700", "6, 1000"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200, 100, Jhon, Andres", "250, 200, Pepe, Pedro", "399, 300, Ulises, Diego", "500, 500, Victor, Luis", "750, 700, Rafa, Guille", "1000, 1000, Juan, Carlos"})
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + " -> " + monto);

            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {

            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
        @MethodSource("montoList")
        void testDebitoCuentaMethodSource(String monto) {
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        static List<String> montoList() {
            return List.of("100", "200", "300", "500", "700", "1000");
        }
    }

    @Nested
    @Tag("timeout")
    class EjemploTimeOutTest {
        @Test
        @Timeout(1)
        void pruebaTimeOut() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeOut2() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        void testTimeOutAssertions() {
            assertTimeout(Duration.ofSeconds(1), () -> {
                TimeUnit.MILLISECONDS.sleep(600);
            });
        }
    }


}