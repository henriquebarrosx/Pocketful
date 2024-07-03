Lembrete de vencimento

Olá, ${account.name}! Aqui estão os seus pagamentos a vencer:

<#list payments as payment>
    ${payment.description} - ${payment.deadlineAt} - ${payment.amount}
</#list>

Já realizou algum desses pagamentos?
Atualize em nossa plataforma
