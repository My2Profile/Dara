$baseUrl = "http://localhost:8080"

function Invoke-ApiTest {
    param($Name, $Method, $Url, $Body, $Token, $ExpectedStatus)

    $headers = @{}

    if($Token) { $headers["Authorization"] = "Bearer $Token" }

    try {
        Invoke-RestMethod -Uri "$baseUrl$Url" -Method $Method -Body ($Body | ConvertTo-Json) `
            -ContentType "application/json" -Headers $headers | Out-Null
    } catch {
        $actual = [int]$_.Exception.Response.StatusCode
    }

    if ($actual -eq $ExpectedStatus) {
        Write-Host "PASS $Name (got $actual)" -ForegroundColor Green
    }
    else {
        Write-Host "ERROR $Name (Expected: $ExpectedStatus, got $actual)" -ForegroundColor Red
    }

}

$adminLogin = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post `
    -Body (@{ email = "admin@gmail.com"; password = "StrongPassword123" } | ConvertTo-Json) `
    -ContentType "application/json"

$adminToken = $adminLogin.token

Invoke-RestMethod -Uri "$baseUrl/api/auth/register" -Method Post `
    -Body (@{ email = "scripttest@gmail.com"; password = "test123" } | ConvertTo-Json) `
    -ContentType "application/json"

$userLogin = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post `
    -Body (@{ email = "scripttest@gmail.com"; password = "test123" } | ConvertTo-Json) `
    -ContentType "application/json"

$userToken = $userLogin.token

$allUsers = Invoke-RestMethod -Uri "$baseUrl/api/admin/getAll" `
    -Headers @{ Authorization = "Bearer $adminToken" }

$testUserId = ($allUsers | Where-Object { $_.email -eq "scripttest@gmail.com" }).Id


# -- Test Cases

Invoke-ApiTest -Name "Invalid Role Rejected" -Method Patch -Url "/api/admin/users/$testUserId/role" `
    -Body @{ role = "SUPERADMIN" } -Token $adminToken -ExpectedStatus 400

Invoke-ApiTest -Name "Non-admin blocked from getAll" -Method Get -Url "/api/admin/getAll" `
    -Body @{} -Token $userToken -ExpectedStatus 403

Invoke-ApiTest -Name "Wrong old password rejected" -Method Patch -Url "/api/users/me/password" `
    -Body @{ oldPassword = "wrong"; newPassword = "right" } -Token $userToken -ExpectedStatus 403

Invoke-ApiTest -Name "Block then verify login fails" -Method Patch -Url "/api/admin/users/$testUserId/active" `
    -Body @{ state = $false } -Token $adminToken -ExpectedStatus 200

try {
    Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post `
        -Body (@{ email = "scripttest@gmail.com"; password = "test123" } | ConvertTo-Json) `
        -ContentType "application/json" | Out-Null
    
    Write-Host "Fail Blcoked user can stil log in" -ForegroundColor Red

} catch {
    $status = [int]$_.Exception.Response.StatusCode
    if($status -eq 403) { Write-Host "PASS Blocked user correctly rejected" -ForegroundColor Green }
    else { Write-Host "FAIL Expected 403, got $status" -ForegroundColor Red }
}