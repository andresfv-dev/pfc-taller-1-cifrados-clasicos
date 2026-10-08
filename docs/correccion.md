## Corrección de `combinaciones`

```scala
def combinaciones(n: Int, a: Int): BigInt =
  if (n == 0) BigInt(1)
  else if (n == 1) BigInt(a)
  else BigInt(a - 1) * combinaciones(n - 1, a)
```

### Qué debe calcular

Queremos contar las palabras de largo $n$ que se pueden formar con $a$ letras
sin que haya dos letras iguales seguidas. La primera letra puede ser cualquiera
($a$ opciones) y cada letra siguiente puede ser cualquiera menos la anterior
($a - 1$ opciones). Por eso:

```math
f(n, a) = \begin{cases} 1 & \text{si } n = 0 \\ a \cdot (a-1)^{n-1} & \text{si } n \geq 1 \end{cases}
```

Hay que mostrar que $combinaciones(n, a) == f(n, a)$ para todo $n \geq 0$.

### Casos base

**$n = 0$:** el programa entra al primer `if` y devuelve $1$. Solo existe una
palabra de largo cero, la vacía, así que $f(0, a) = 1$. Coinciden.

**$n = 1$:** el programa entra al segundo `if` y devuelve $a$. Con una sola
letra cualquiera sirve, y $f(1, a) = a \cdot (a-1)^0 = a$. Coinciden.

### Caso de inducción

Suponemos que el programa funciona para un $k \geq 1$, es decir:

```math
combinaciones(k, a) = a \cdot (a-1)^{k-1} \quad \text{(hipótesis de inducción)}
```

Para $n = k + 1$ el programa no entra a ningún caso base, así que calcula:

```math
combinaciones(k+1, a) \rightarrow (a-1) \cdot combinaciones(k, a)
```

Usando la hipótesis:

```math
(a-1) \cdot a \cdot (a-1)^{k-1} = a \cdot (a-1)^{k} = f(k+1, a)
```

En palabras: las palabras de largo $k+1$ son las de largo $k$ con una letra más
al final, y esa letra tiene $a - 1$ opciones porque no puede repetir la
anterior.

### Conclusión

Los casos base se cumplen y, si el programa funciona para $k$, también funciona
para $k + 1$. Por inducción, $combinaciones(n, a) == f(n, a)$ para todo
$n \geq 0$.


## Corrección de `vigenere`

```scala
def vigenere(m: Mensaje, clave: Clave): Mensaje = {
  def cifrar(resto: Mensaje, i: Int): Mensaje = {
    if (resto.isEmpty) ""
    else {
      val c = resto.head
      if (esMinuscula(c)) {
        val correr = clave(i % clave.length) - primera
        val nueva = ((c - primera + correr) % letras + primera).toChar
        nueva + cifrar(resto.tail, i + 1)
      } else {
        c + cifrar(resto.tail, i)
      }
    }
  }
  if (clave.isEmpty) m else cifrar(m, 0)
}
```

### Qué debe calcular

Sea $L$ la longitud de la clave. Vigenère corre cada letra minúscula del
mensaje según una letra de la clave: la primera minúscula usa la letra $0$ de
la clave, la segunda usa la letra $1$, y así, volviendo al inicio de la clave
cuando se acaba. Lo que no es minúscula se copia igual y no gasta letra de la
clave.

Llamamos $g(resto, i)$ al resultado correcto de cifrar $resto$ cuando ya se
han usado $i$ letras de la clave. Lo que se pide es
$vigenere(m, clave) = g(m, 0)$.

### Clave vacía

Si la clave es vacía no hay corrimiento, y el programa devuelve $m$ sin
cambios. Eso es lo esperado.

### Inducción sobre el largo de $resto$

Hay que mostrar que $cifrar(resto, i) == g(resto, i)$ para cualquier $i$.

**Caso base:** $resto$ vacío. El programa devuelve `""`, y cifrar un mensaje
vacío da el mensaje vacío. Coinciden.

**Caso de inducción:** $resto = c$ seguido de $r$, donde $r$ tiene $k$
caracteres. Suponemos que $cifrar(r, j) == g(r, j)$ para cualquier $j$
(hipótesis de inducción).

- Si $c$ es minúscula, el programa la corre según la letra
  $clave(i \bmod L)$, que es justo la que le toca, y sigue con
  $cifrar(r, i+1)$. Por la hipótesis, eso es $g(r, i+1)$: el resto se cifra
  bien empezando en la siguiente letra de la clave.

```math
cifrar(c\,r, i) = correr(c, clave(i \bmod L)) + g(r, i+1) = g(c\,r, i)
```

- Si $c$ no es minúscula, el programa la copia y sigue con $cifrar(r, i)$, sin
  avanzar en la clave. Por la hipótesis, eso es $g(r, i)$.

```math
cifrar(c\,r, i) = c + g(r, i) = g(c\,r, i)
```

En los dos casos el programa da el resultado correcto.

### Conclusión

Por inducción, $cifrar(resto, i) == g(resto, i)$ para todo mensaje y todo $i$.
En particular, $vigenere(m, clave) = cifrar(m, 0) = g(m, 0)$, que es el
cifrado correcto.
