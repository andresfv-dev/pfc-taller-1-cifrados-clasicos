package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

/**
 * Pruebas propias de los puntos 3 y 4, distintas de los ejemplos del
 * enunciado y de las pruebas que trae el material.
 */
@RunWith(classOf[JUnitRunner])
class PruebasPuntos3y4Test extends AnyFunSuite {

  val c = new CifradosClasicos()
  import c._

  // Punto 3: frecuencias ------------------------------------------------------

  test("frecuencias propia: las mayúsculas no se cuentan") {
    assert(frecuencias("Casa CASA") == List(('a', 2), ('s', 1)))
  }

  test("frecuencias propia: las letras con tilde y la ñ no se cuentan") {
    assert(frecuencias("niño ñandú") ==
      List(('n', 2), ('a', 1), ('d', 1), ('i', 1), ('o', 1)))
  }

  test("frecuencias propia: si todas empatan salen en orden alfabético") {
    // Aparecen al revés (z, y, x) pero deben salir ordenadas (x, y, z).
    assert(frecuencias("zyxzyx") == List(('x', 2), ('y', 2), ('z', 2)))
  }

  test("frecuencias propia: la suma de las veces es el total de letras") {
    // "el mensaje secreto" tiene 2 + 7 + 7 = 16 letras minúsculas.
    assert(frecuencias("el mensaje secreto").map(par => par._2).sum == 16)
  }

  test("frecuencias propia: un mensaje largo no desborda la pila") {
    val largo = "ab" * 50000
    assert(frecuencias(largo) == List(('a', 50000), ('b', 50000)))
  }

  // Punto 4: desplazamientoProbable y romperCesar ------------------------------

  test("desplazamientoProbable propia: encuentra el desplazamiento 10") {
    assert(desplazamientoProbable(cesar("este ejemplo tiene muchas e", 10)) == 10)
  }

  test("desplazamientoProbable propia: siempre queda entre 0 y 25") {
    // Con -1 la e pasa a ser d (25 después de e); con 30 es lo mismo que con 4.
    assert(desplazamientoProbable(cesar("eee", -1)) == 25)
    assert(desplazamientoProbable(cesar("eee", 30)) == 4)
  }

  test("romperCesar propia: recupera un mensaje con espacios y signos") {
    val original = "ese tren se detiene, verdad?"
    assert(romperCesar(cesar(original, 15)) == original)
  }

  test("romperCesar propia: las mayúsculas pasan sin cambio") {
    val original = "Pepe se fue de este pueblo"
    assert(romperCesar(cesar(original, 4)) == original)
  }

  test("romperCesar propia: falla cuando la letra más frecuente es la o") {
    // En "solo como pollo" la o aparece 6 veces y la e ninguna.
    // El método cree que la o cifrada era una e y se equivoca.
    val original = "solo como pollo"
    assert(romperCesar(cesar(original, 5)) == "iebe sece febbe")
  }
}
