# Informe de corrección

Fundamentos de Programación Funcional y Concurrente.
Taller 1 — Cifrados clásicos con recursión.

## 1. César con recursión lineal: `cesar`

### Especificación

Sea $L = \{a, b, \ldots, z\}$ el conjunto de las 26 letras minúsculas, y sea
$\text{ord}(c)$ el código numérico del carácter $c$ (con
$\text{ord}(a) = 97$, que en el programa es la constante `primera`).

Cifrar un carácter $c$ con desplazamiento $k \in \mathbb{Z}$ es correrlo $k$
posiciones en el alfabeto, de forma circular, si es una letra minúscula, y
dejarlo igual si no lo es:

```math
\text{cifrar}(c, k) =
\begin{cases}
\text{chr}\big((\text{ord}(c) - 97 + k) \bmod 26 + 97\big) & \text{si } c \in L \\
c & \text{si } c \notin L
\end{cases}
```

Sea $f : \text{Mensaje} \times \mathbb{Z} \to \text{Mensaje}$ la función que
cifra un mensaje completo, carácter por carácter. Para un mensaje
$m = c_1 c_2 \ldots c_n$:

```math
f(c_1 c_2 \ldots c_n,\ k) = \text{cifrar}(c_1, k)\ \text{cifrar}(c_2, k) \ldots \text{cifrar}(c_n, k)
```

En particular, $f(\text{""}, k) = \text{""}$.

### Programa

Sea $P_f$ el siguiente programa en Scala:

```scala
def cesar(m: Mensaje, k: Int): Mensaje = {
  if (m.isEmpty) { "" }
  else {
    val c = m.head
    val charCifrado = if (c.isLetter && esMinuscula(c)) {
      (((c.toInt - primera + k) % 26 + letras) % 26 + primera).toChar
    } else {
      c
    }
    charCifrado + cesar(m.tail, k)
  }
}
```

### El cifrado de un carácter es correcto

Primero se muestra que `charCifrado` es exactamente $\text{cifrar}(c, k)$.

- Si $c \notin L$, la condición `c.isLetter && esMinuscula(c)` es falsa y
  `charCifrado` $= c = \text{cifrar}(c, k)$.
- Si $c \in L$, sea $x = \text{ord}(c) - 97 + k$. En Scala, `x % 26` puede ser
  negativo cuando $x < 0$ (por ejemplo, si $k$ es negativo), pero siempre
  cumple $-26 < x \,\%\, 26 < 26$. Entonces $x \,\%\, 26 + 26$ es positivo, y

```math
(x \,\%\, 26 + 26) \,\%\, 26 = x \bmod 26 \in \{0, 1, \ldots, 25\}
```

  Por lo tanto `charCifrado`
  $= \text{chr}\big((\text{ord}(c) - 97 + k) \bmod 26 + 97\big) = \text{cifrar}(c, k)$,
  que siempre es una letra de $L$.

### Demostración

Vamos a demostrar, por inducción sobre la longitud $n$ del mensaje, que:

```math
\forall n \in \mathbb{N},\ \forall k \in \mathbb{Z} : P_f(c_1 c_2 \ldots c_n,\ k) == f(c_1 c_2 \ldots c_n,\ k)
```

**Caso base:** $n = 0$, es decir, $m = \text{""}$.

```math
P_f(\text{""}, k) \rightarrow \text{if } (\text{"".isEmpty})\ \text{""} \text{ else } \ldots \rightarrow \text{""}
```

Por otro lado, $f(\text{""}, k) = \text{""}$. Entonces
$P_f(\text{""}, k) == f(\text{""}, k)$.

**Caso de inducción:** $n = j + 1$, $j \geq 0$. Sea
$m = c_1 c_2 \ldots c_{j+1}$, de modo que $m.\text{head} = c_1$ y
$m.\text{tail} = c_2 \ldots c_{j+1}$, que tiene longitud $j$. Hay que demostrar:

```math
P_f(c_2 \ldots c_{j+1},\ k) == f(c_2 \ldots c_{j+1},\ k) \rightarrow P_f(c_1 c_2 \ldots c_{j+1},\ k) == f(c_1 c_2 \ldots c_{j+1},\ k)
```

Usando el modelo de sustitución, como $m$ no es vacío:

```math
P_f(m, k) \rightarrow \text{if } (m.\text{isEmpty})\ \text{""} \text{ else } \{\ldots\ \text{charCifrado} + P_f(m.\text{tail}, k)\}
```

```math
\rightarrow \text{cifrar}(c_1, k) + P_f(c_2 \ldots c_{j+1},\ k)
```

Usando la hipótesis de inducción (HI):

```math
\rightarrow \text{cifrar}(c_1, k) + f(c_2 \ldots c_{j+1},\ k)
```

```math
= \text{cifrar}(c_1, k)\ \text{cifrar}(c_2, k) \ldots \text{cifrar}(c_{j+1}, k) = f(c_1 c_2 \ldots c_{j+1},\ k)
```

Por lo tanto, $P_f(m, k) == f(m, k)$.

Concluimos por inducción que:

```math
\forall m \in \text{Mensaje},\ \forall k \in \mathbb{Z} : \text{cesar}(m, k) == f(m, k)
```

## 2. César con recursión de cola: `cesarCola`

### Especificación

La función que se calcula es la misma $f$ de la sección anterior: dado un
mensaje $M = c_1 c_2 \ldots c_n$ y un desplazamiento $k$,

```math
f(c_1 c_2 \ldots c_n,\ k) = \text{cifrar}(c_1, k)\ \text{cifrar}(c_2, k) \ldots \text{cifrar}(c_n, k)
```

De esta definición sale una propiedad que se usa más abajo: cifrar un mensaje
partido en dos es lo mismo que cifrar cada parte y pegarlas,

```math
f(p\, q,\ k) = f(p, k) + f(q, k)
```

porque $f$ cifra cada carácter por separado, sin mirar a los demás.

### Programa

Sea $P_f$ el siguiente programa en Scala:

```scala
@tailrec
final def cesarCola(m: Mensaje, k: Int, acc: Mensaje = ""): Mensaje = {
  if (m.isEmpty) { acc }
  else {
    val c = m.head
    val charCifrado = if (c.isLetter && esMinuscula(c)) {
      (((c.toInt - primera + k) % 26 + letras) % 26 + primera).toChar
    } else {
      c
    }
    cesarCola(m.tail, k, acc + charCifrado)
  }
}
```

La llamada recursiva es lo último que hace la función, y la anotación
`@tailrec` hace que el compilador lo verifique. Por eso el programa es un
proceso iterativo: `cesarCola` cumple el papel de la función `iter`, y el
llamado inicial `cesarCola(M, k)` usa el valor por defecto `acc = ""`.

El cálculo de `charCifrado` es el mismo de `cesar`, así que, por lo mostrado
en la sección 1, `charCifrado` $= \text{cifrar}(c, k)$.

### Proceso iterativo

Este programa implementa el siguiente proceso iterativo:

- Un estado $s = (m, k, acc)$, donde $m$ es la parte del mensaje que falta
  por cifrar y $acc$ es lo que ya se cifró. El desplazamiento $k$ no cambia.
- El estado inicial es $s_0 = (M, k, \text{""}) = (c_1 c_2 \ldots c_n,\ k,\ \text{""})$.
- $(m, k, acc)$ es final si $m$ es vacío, y la respuesta es $acc$.
- La invariante de ciclo es: lo que falta es una cola de $M$, y lo acumulado
  es el cifrado de todo lo que va antes de esa cola.

```math
\text{Inv}(m, k, acc) \equiv m = c_i \ldots c_n \land acc = f(c_1 \ldots c_{i-1},\ k), \quad 1 \leq i \leq n + 1
```

  Cuando $i = n + 1$, $m$ es vacío.

- $\text{transformar}((m, k, acc)) = (m.\text{tail},\ k,\ acc + \text{cifrar}(m.\text{head}, k))$.

Ahora, demostramos los puntos mencionados:

**1.** $\text{Inv}(s_0)$: el estado inicial cumple la condición invariante.

Con $i = 1$:

```math
s_0 = (c_1 \ldots c_n,\ k,\ \text{""}) \implies m = c_1 \ldots c_n \land \text{""} = f(\text{""}, k)
```

Lo que va antes de $c_1$ es el mensaje vacío, y su cifrado es $\text{""}$.

**2.** La invariante se mantiene con la transformación de estados,
$(s_i \neq s_f \land \text{Inv}(s_i)) \rightarrow \text{Inv}(\text{transformar}(s_i))$:

Si $s_i$ no es final, $m$ no es vacío, y por la invariante

```math
m = c_i\, c_{i+1} \ldots c_n \land acc = f(c_1 \ldots c_{i-1},\ k)
```

con $i \leq n$. Al transformar el estado:

1. Primer cambio, $m = m.\text{tail} = c_{i+1} \ldots c_n$: lo que falta
   sigue siendo una cola de $M$, una posición más adelante.
2. Segundo cambio, $acc = acc + \text{cifrar}(c_i, k)$, entonces

```math
acc = f(c_1 \ldots c_{i-1},\ k) + \text{cifrar}(c_i, k) = f(c_1 \ldots c_i,\ k)
```

3. El nuevo estado cumple la invariante con $i + 1$ en lugar de $i$:

```math
\text{Inv}(c_{i+1} \ldots c_n,\ k,\ f(c_1 \ldots c_i,\ k))
```

**3.** $\text{Inv}(s_f) \rightarrow \text{respuesta}(s_f) == f(M, k)$

En el estado final $m$ es vacío, así que $i = n + 1$:

```math
\text{Inv}(\text{""}, k, acc) \rightarrow acc = f(c_1 \ldots c_n,\ k) = f(M, k)
```

y la respuesta del programa en ese estado es justamente $acc$.

**4.** En cada paso, $m$ pierde su primer carácter, es decir, su longitud
disminuye en 1, acercándose a ser vacío. Después de $n$ iteraciones,
$m = \text{""}$ y se alcanza el estado final.

Esto implica que:

```math
P_f(M, k) == \text{cesarCola}(M, k, \text{""}) == f(M, k)
```
