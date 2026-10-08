package taller

import scala.annotation.tailrec

/**
 * Taller 1 — cifrados clásicos con recursión.
 *
 * Solo se cifran las 26 letras minúsculas del alfabeto inglés; cualquier otro
 * carácter se copia sin cambio.
 */
class CifradosClasicos {

  type Mensaje = String
  type Clave = String

  // Una frecuencia asocia cada letra con las veces que aparece.
  type Frecuencias = List[(Char, Int)]

  val letras = 26
  val primera = 'a'.toInt

  def esMinuscula(c: Char): Boolean = c >= 'a' && c <= 'z'

  // Punto 1 -------------------------------------------------------------------

  /** César con recursión lineal: una operación pendiente por letra. */
  def cesar(m: Mensaje, k: Int): Mensaje = {
     if(m.isEmpty){""}
     else {
       val c = m.head
       //evaluo si es letra y si es minuscula
       val charCifrado = if (c.isLetter && esMinuscula(c)) {
         (((c.toInt - primera + k) % 26 + letras) % 26 + primera).toChar
       } else {// la misma letra o caracter
         c
         }
       charCifrado + cesar(m.tail, k)
     }
  }

  /**
   * El mismo César como proceso iterativo: espacio constante.
   * Cuando la función esté escrita, anótela con @tailrec: el compilador
   * comprueba que la llamada recursiva sea lo último que hace.
   */
  @tailrec
  final def cesarCola(m: Mensaje, k: Int, acc: Mensaje = ""): Mensaje = {
      if(m.isEmpty){acc}
      else {
        val c = m.head
        val charCifrado = if (c.isLetter && esMinuscula(c)) {
          (((c.toInt - primera + k) % 26 + letras) % 26 + primera).toChar
        } else {
          c
        }
        cesarCola (m.tail, k,acc + charCifrado)
      }
    }



  // Punto 3 -------------------------------------------------------------------

  /**
   * Cuenta las letras minúsculas del mensaje, de mayor a menor frecuencia y,
   * en empate, en orden alfabético. El recorrido es recursivo de cola.
   */
  def frecuencias(m: Mensaje): Frecuencias = {

    // le suma 1 a la letra en la lista; si la letra no esta, la agrega con 1
    def sumarUno(letra: Char, lista: Frecuencias): Frecuencias = {
      if (lista.isEmpty) {List((letra, 1))}
      else if (lista.head._1 == letra) {(letra, lista.head._2 + 1) :: lista.tail}
      else {lista.head :: sumarUno(letra, lista.tail)}
    }

    // recorre el mensaje letra por letra y va guardando el conteo en acc
    @tailrec
    def contar(resto: Mensaje, acc: Frecuencias): Frecuencias = {
      if (resto.isEmpty) {acc}
      else {
        val c = resto.head
        //solo se cuentan las letras minusculas
        val nuevoAcc = if (esMinuscula(c)) {
          sumarUno(c, acc)
        } else {// los demas caracteres no se cuentan
          acc
        }
        contar(resto.tail, nuevoAcc)
      }
    }

    // ordena de mayor a menor frecuencia y, si empatan, en orden alfabetico
    contar(m, List()).sortBy(par => (-par._2, par._1))
  }

  // Punto 4 -------------------------------------------------------------------

  /**
   * Supone que la letra más frecuente del mensaje cifrado es la 'e' del
   * original y devuelve la distancia entre las dos. Sin letras, cero.
   */
  def desplazamientoProbable(m: Mensaje): Int = {
    val lista = frecuencias(m)
    if (lista.isEmpty) {0}
    else {
      val masFrecuente = lista.head._1
      // distancia de 'e' a la letra mas frecuente, siempre entre 0 y 25
      ((masFrecuente.toInt - 'e'.toInt) % letras + letras) % letras
    }
  }

  /** Descifra corriendo el mensaje hacia atrás el desplazamiento estimado. */
  def romperCesar(m: Mensaje): Mensaje = {
    val k = desplazamientoProbable(m)
    cesar(m, -k)
  }

  // Punto 5 -------------------------------------------------------------------

  /**
   * Cuántos mensajes de longitud n se forman con a letras sin dos iguales
   * seguidas.
   */
  def combinaciones(n: Int, a: Int): BigInt = {
    if(n == 0) BigInt(1)
    else if (n == 1) BigInt(a)
    else BigInt(a - 1) * combinaciones(n-1, a)
  }

  /**
   * Vigenère: cada letra se corre según la letra de la clave que le toca. Lo
   * que no es letra minúscula se copia y no consume clave.
   */
  def vigenere(m: Mensaje, clave: Clave): Mensaje = ???
}
