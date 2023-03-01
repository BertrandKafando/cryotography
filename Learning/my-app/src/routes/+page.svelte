<script>
    import Encap from "./encap.svelte";
    import Event from "./event.svelte";
    import Prop from "./prop.svelte";
    import Bind from "./bind.svelte";
    import Keypad from "./keypad.svelte";
    import Writetable from "./stores/writetable.svelte";
    import { count, countCustom } from "./stores/store";
    import { onDestroy } from "svelte";
    import Readable from "./stores/readable.svelte";

    let pin;
    $: view = pin ? pin.replace(/\d(?!$)/g, "•") : "enter your pin";

    function handleSubmit() {
        alert(`submitted ${pin}`);
    }

    let countValue = 0;
    const subscribe = count.subscribe((value) => {
        countValue = value;
    });

    // unsubscribe when the component is destroyed
    onDestroy(subscribe);
</script>

<div class="container">
    <Prop name="Aziz" />
    <Encap />
    <Event />
    <Bind />

    <h1 style="color: {pin ? '#333' : '#ccc'}">{view}</h1>
    <Keypad bind:value={pin} on:submit={handleSubmit} />
    <hr />
    <h1>Stores</h1>
    <h1>The count is {countValue}</h1>
    <h2>custom store count is {$countCustom}</h2>
    <Writetable />

    <h2>Times</h2>
    <Readable />
</div>

<style>
    .container {
        max-width: 600px;
        margin: 0 auto;
        padding: 1rem;
    }
</style>
