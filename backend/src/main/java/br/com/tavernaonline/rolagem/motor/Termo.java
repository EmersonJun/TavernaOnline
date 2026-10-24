package br.com.tavernaonline.rolagem.motor;

public sealed interface Termo permits GrupoDados, Modificador {

    int sinal();
}
