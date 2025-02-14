(ns hospital.aula1
  (:use clojure.pprint)
  (:require [schema.core :as s]))

(defn adiciona-paciente
  [pacientes paciente]
  (if-let [id (:id paciente)]
    (assoc pacientes id paciente)
    (throw (ex-info "Paciente não possui id" {:paciente paciente}))))

; 1a Forma
; Colocando pacientes em um vetor
(defn testa-uso-de-pacientes []
  (let [guilherme {:id 15 :nome "Guilherme"}
        lucia {:id 20 :nome "Lucia"}
        paulo {:id 25 :nome "Paulo"}]
    (pprint [guilherme lucia paulo])))

(testa-uso-de-pacientes)

; 2a Forma
; Utilizando 'reduce'-> retorno como mapa
(defn testa-uso-de-pacientes []
  (let [guilherme {:id 15 :nome "Guilherme"}
        lucia {:id 20 :nome "Lucia"}
        paulo {:id 25 :nome "Paulo"}
        pacientes (reduce adiciona-paciente {} [guilherme, lucia, paulo])]
    (pprint pacientes)))

(testa-uso-de-pacientes)

; 3a Forma
; Atrelando visitas ao paciente
(defn adiciona-visita
  [visitas, paciente, novas-visitas]
  (if (contains? visitas paciente)
    (update visitas paciente concat novas-visitas)          ; p/ alterar o valor de um mapa
    (assoc visitas paciente novas-visitas)))                ; p/ concatenar as novas visitas no mapa

(defn testa-uso-de-pacientes []
  (let [guilherme {:id 15 :nome "Guilherme"}
        lucia {:id 20 :nome "Lucia"}
        paulo {:id 25 :nome "Paulo"}
        pacientes (reduce adiciona-paciente {} [guilherme, lucia, paulo])
        visitas {}]
    (pprint pacientes)
    (pprint (adiciona-visita visitas 15 ["01/01/2019"]))
    (pprint (adiciona-visita visitas 20 ["01/02/2019", "01/01/2020"]))
    (pprint (adiciona-visita visitas 15 ["01/03/2019"]))
    ))

(testa-uso-de-pacientes)

(defn imprime-relatorio-de-paciente [visitas, paciente]
  (println "Visitas do paciente" paciente "são" (get visitas paciente)))

; Reformatando com shadowing
(defn testa-uso-de-pacientes []
  (let [guilherme {:id 15 :nome "Guilherme"}
        lucia {:id 20 :nome "Lucia"}
        paulo {:id 25 :nome "Paulo"}

        ; variação com 'reduce' -> melhor opção
        pacientes (reduce adiciona-paciente {} [guilherme, lucia, paulo])

        ; variação com shadowing -> verboso
        ; visitas são agrupadas pelo id do paciente
        visitas {}
        visitas (adiciona-visita visitas 15 ["01/01/2019"])
        visitas (adiciona-visita visitas 20 ["01/02/2019", "01/01/2020"])
        visitas (adiciona-visita visitas 15 ["01/03/2019"])]
    (pprint pacientes)
    (pprint visitas)

    ; assim, o retorno será nil -> pois o símbolo 'paciente' está sendo usado c/ vários significados diferentes
    (imprime-relatorio-de-paciente visitas lucia)
    (println (get visitas 20))))

(testa-uso-de-pacientes)

; Assim que o 'validate' valida o tipo passado, devolve o próprio valor
(pprint (s/validate Long 15))
;(pprint (s/validate Long "guilherme"))

(s/set-fn-validation! true)

; Variação do defn do próprio schema (macro que define uma função)
(s/defn teste-simples [x :- Long]
  (println x))
(teste-simples 30)
;(teste-simples "guilherme")

(s/defn imprime-relatorio-de-paciente
  [visitas, paciente :- Long]
  (println "Visitas do paciente" paciente "são" (get visitas paciente)))

; Conseguimos o erro em tempo de execução que diz que o valor passado como parâmetro não condiz com o schema Long
; Com (:-) dizemos de maneira declarativa quais são os esquemas que estamos esperando como parâmetros das funções
; (testa-uso-de-pacientes)

(s/defn novo-paciente
  [id :- Long, nome :- s/Str]
  {:id id, :nome nome})

(pprint (novo-paciente 15 "Guilherme"))
;(pprint (novo-paciente "Guilherme" 15))

; Qual cenário faz sentido para mantermos nossas validações de schemas?
; Ativo em desenvolvimento e testes com boa qualidade, desativado em produção exceto nas camadas de entrada de
; dados externos
; Temos as garantias dos schemas nas bordas dos sistemas em produção e dentro do sistema em testes com qualidade.