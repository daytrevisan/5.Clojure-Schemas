(ns hospital.aula2
  (:use clojure.pprint)
  (:require [schema.core :as s]))

(s/set-fn-validation! true)

;(s/defrecord Paciente
;  [id :- Long, nome :- s/Str])

;(pprint (Paciente. 15 "Guilherme"))
;(pprint (Paciente. "15" "Guilherme"))
;
;(pprint (->Paciente 15 "Guilherme"))
;(pprint (->Paciente "15" "Guilherme"))

; Record + map
; Record é expansível -> pode receber mais do que o esperado
;(pprint (map->Paciente {15 "Guilherme"}))
;(pprint (map->Paciente {"15" "Guilherme"}))

; Com caixa alta, definimos como -schema-
; Não precisamos definir como (:-)
(def PacienteSchema
  "Schema de um paciente"
  {:id s/Num, :nome s/Str})                                 ; assim direcionamos qual valor pertence a cada chave

(pprint (s/explain PacienteSchema))
(pprint (s/validate PacienteSchema {:id 15, :nome "Guilherme"}))

; Typo é pego pelo schema, mas poderíamos argumentar que esse tipo de erro
; seria pego em testes automatizados com boa cobertura
;(pprint (s/validate Paciente {:id 15, :nome "Guilherme"}))

; mas entra a questão de QUERER ser forward compatible ou NÃO
; entender esse trade-off
; sistemas externos não quebrarão ao adicionar campos novos (foward compatible)
; no nosso validate não estamos sendo forward compatible (pode ser interessante quando quero analisar mudanças)
; (pprint (s/validate Paciente {:id, :nome "Guilherme", :plano [:raio-x]}))

; chaves que são keywords em schemas são por padrão OBRIGATÓRIAS
;(pprint (s/validate Paciente {:id 15}))

; Tipo de retorno com schema -> força a validação na saída da função
; (s/defn novo-paciente :-Paciente
;  [id :- s/Num, nome :- s/Str]
;  { :id id, :nome nome, plano [] })

(s/defn novo-paciente :- PacienteSchema
  [id :- s/Num, nome :- s/Str]
  {:id id, :nome nome})

(pprint (novo-paciente 15 "Guilherme"))



; Função pura, simples, fácil de testar
(defn estritamente-positivo? [x]
  (> x 0))

; Criando um esquema (EstritamentePositivo) que chama um predicado
(def EstritamentePositivo (s/pred estritamente-positivo?))

(pprint (s/validate EstritamentePositivo 15))
;(pprint (s/validate EstritamentePositivo 0))
;(pprint (s/validate EstritamentePositivo -15))

(def EstritamentePositivo (s/pred estritamente-positivo?))



(def PacienteSchema
  "Schema de um paciente"
  {:id s/constrained s/Int pos?, :nome s/Str})
; Já existe em Clojure as funções <POS> p/ validar se é positivo
; E a função <POS-INT> que valida se é inteiro e positivo

(pprint (s/validate PacienteSchema {:id 15, :nome "Guilherme"}))
;(pprint (s/validate PacienteSchema {:id -15, :nome "Guilherme"}))
;(pprint (s/validate PacienteSchema {:id 0, :nome "Guilherme"}))

; Usando lambda -> dificulta leitura das mensagens de erro
; Lambda dentro dos schemas -> nomes confusos e a legibilidade do schema se perde
; Também perdemos a facilidade de testar o lambda isoladamente

(def PacienteSchema
  "Schema de um paciente"
  {:id (s/constrained s/Int #(> % 0) 'inteiro-estritamente-positivo'), :nome s/Str})

(pprint (s/validate PacienteSchema {:id 15, :nome "Guilherme"}))
(pprint (s/validate PacienteSchema {:id -15, :nome "Guilherme"}))
(pprint (s/validate PacienteSchema {:id 0, :nome "Guilherme"}))


; PREDICATES com SCHEMAS são usados para -> definir regras extras que -limitam os valores- de nossos dados