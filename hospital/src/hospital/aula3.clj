(ns hospital.aula3
                                          (:use clojure.pprint)
                                          (:require [schema.core :as s]))

(s/set-fn-validation! true)

(def PosInt (s/pred pos-int? 'inteiro-positivo))

(def Paciente
  {:id PosInt, :nome s/Str})

(s/defn novo-paciente :- Paciente
  [id :- PosInt
   nome :- s/Str]
  {:id id, :nome nome})

(pprint (novo-paciente 15, "Guilherme"))
;(pprint (novo-paciente -5, "Guilherme"))

(defn maior-ou-igual-a-zero? [x] (>= x 0))
(def ValorFinanceiro (s/constrained s/Num maior-ou-igual-a-zero?))

(def Pedido
  {:paciente     Paciente
   :valor        ValorFinanceiro
   :procedimento s/Keyword})

; será que faz sentido "mini-schema" como aliases?
;(def Procedimento s/Keyword)

(s/defn novo-pedido :- Pedido
  [paciente     :- Paciente,
   valor        :- ValorFinanceiro,
   procedimento :- s/Keyword]
  {:paciente paciente, :valor valor, :procedimento procedimento})

(pprint (novo-pedido (novo-paciente 15, "Guilherme"), 15.53, :raio-x))
;(pprint (novo-pedido (novo-paciente 15, "Guilherme"), -15.53, :raio-x))



(def Numeros [s/Num])
(pprint (s/validate Numeros [15]))
(pprint (s/validate Numeros [15, 19]))
(pprint (s/validate Numeros [15, 18, 192, 23.2]))
(pprint (s/validate Numeros [0]))
; nil não é número, não faz sentido
;(pprint (s/validate Numeros [nil]))
(pprint (s/validate Numeros []))

; nil é como se fosse uma sequência vazia de números
(pprint (s/validate Numeros nil))

; nil não é s/Num
;(pprint (s/validate s/Num nil))
; nil é [s/Num]
(pprint (s/validate [s/Num] nil))

(def Plano [s/Keyword])
(pprint (s/validate Plano [:raio-x]))


(def Paciente
  {:id PosInt, :nome s/Str, :plano Plano})

(pprint (s/validate Paciente {:id 15, :nome "Guilherme", :plano [:raio-x, :ultrassom]}))
(pprint (s/validate Paciente {:id 15, :nome "Guilherme", :plano [:raio-x]}))
(pprint (s/validate Paciente {:id 15, :nome "Guilherme", :plano []}))
(pprint (s/validate Paciente {:id 15, :nome "Guilherme", :plano nil}))

; -plano- é uma keyword obrigatória no mapa, mas ela pode ter uma vetor vazio (nil, [])
;(pprint (s/validate Paciente {:id 15, :nome "Guilherme"}))