<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lembrete de Pagamentos</title>
    <style>
        /* Estilos para o e-mail */
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            padding: 20px;
        }

        .background {
            display: flex;
            justify-content: center;
            align-items: center;
            max-width: 600px;
            margin: 0 auto;
            height: 80px;
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
            padding: 20px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            background-color: #3155D2;
        }

        @media (max-width: 600px) {
            .background {
                height: 50px;
            }
        }

        .background h1 {
            font-size: 28px;
            color: #FFFFFF;
            text-align: center;
        }

        @media (max-width: 600px) {
            .background h1 {
                font-size: 21px;
            }
        }

        .container {
            max-width: 600px;
            margin: 0 auto;
            background-color: #ffffff;
            border-bottom-left-radius: 10px;
            border-bottom-right-radius: 10px;
            padding: 20px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);

            display: flex;
            flex-direction: column;
            gap: 32px;
        }

        .greeting-container {
            display: flex;
            flex-direction: column;
            gap: 2px;
        }

        .greeting-container h1 {
            font-size: 21px;
        }

        .greeting-container span {
            font-size: 16px;
        }

        .payments {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .payment-item {
            padding: 10px;
            border: 1px dashed #ddd;
            border-radius: 5px;
            background-color: #F7F7F7;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .overdue {
            color: #ef5350 !important;
        }

        .left-side {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .description {
            font-size: 16px;
            color: #6b6b6b;
            font-weight: 600;
        }

        .deadlineAt {
            font-size: 14px;
            color: #747474;
        }

        .payment-item .value {
            font-size: 16px;
            color: #373737;
        }

        .considerations {
            display: flex;
            justify-content: center;
            align-items: center;
            flex-direction: column;
        }

        .considerations p {
            font-size: 16px;
            font-weight: 600;
            text-align: center;
        }

        .considerations span {
            font-size: 14px;
            margin-top: -8px;
        }

        @media (max-width: 600px) {
            .considerations p {
                font-size: 14px;
            }

            .considerations span {
                font-size: 12px;
            }
        }
    </style>
</head>
<body>

<div class="background">
    <h1>Lembrete de vencimento</h1>
</div>

<div class="container">
    <div class="greeting-container">
        <h1>Olá, ${account.name}!</h1>
        <span>Aqui estão os seus pagamentos a vencer:</span>
    </div>

    <div class="payments">
        <#list payments as payment>
            <div class="payment-item">
                <div class="left-side">
                    <#if payment.overdue>
                        <span class="description overdue">${payment.description}</span>
                    <#else>
                        <span class="description">${payment.description}</span>
                    </#if>

                    <#if payment.overdue>
                        <span class="deadlineAt overdue">${payment.deadlineAt}</span>
                    <#else>
                        <span class="deadlineAt">${payment.deadlineAt}</span>
                    </#if>
                </div>

                <#if payment.overdue>
                    <p class="value overdue"><strong>${payment.amount}</strong></p>
                <#else>
                    <p class="value"><strong>${payment.amount}</strong></p>
                </#if>
            </div>
        </#list>
    </div>

    <div class="considerations">
        <p>Já realizou algum desses pagamentos?</p>
        <span>Atualize em nossa plataforma</span>
    </div>
</div>
</body>
</html>
