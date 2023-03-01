<script>
    let name = "Bertrand";

    $: console.log(name);
</script>

<h1>Forms</h1>
<div>
    <label for="name">Name</label>
    <input type="text" id="name" bind:value={name} />
</div>
